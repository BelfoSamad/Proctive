package net.roeia.proctive.utils.adapters

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.InvocationTargetException

class BasicRecyclerViewAdapter<MODEL, VH : BasicRecyclerViewAdapter.BaseViewHolder<MODEL>> private constructor(
    @LayoutRes
    private val layoutId: Int,
    private val itemList: MutableList<MODEL>,
    private val vhClass: Class<VH>,
    private val bundle: Bundle,
    private val listener: BaseListener
) : RecyclerView.Adapter<BasicRecyclerViewAdapter.BaseViewHolder<MODEL>>() {

    private constructor(builder: Builder<MODEL, VH>) : this(
        builder.layoutId,
        builder.itemList,
        builder.vhClass,
        builder.bundle,
        builder.listener
    )

    companion object {
        inline fun <model, reified vh : BaseViewHolder<model>> build(
            @LayoutRes layoutId: Int,
            itemList: MutableList<model>,
            bundle: Bundle,
            listener: BaseListener
        ) = Builder(layoutId, itemList, vh::class.java, bundle, listener).build()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<MODEL> {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layoutId,
            parent,
            false
        )
        var vh: VH? = null
        try {
            vh = vhClass.constructors[0].newInstance(binding) as VH
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }

        return if (vh == null) {
            throw NullPointerException()
        } else {
            vh
        }
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: BaseViewHolder<MODEL>, position: Int) {
        holder.onBindViewHolder(itemList[position], bundle, position, listener)
    }

    fun addItem(model: MODEL) {
        itemList.add(model)
        notifyItemInserted(itemList.size - 1)
    }

    fun addItemIn(model: MODEL, position: Int) {
        itemList.add(position, model)
        notifyItemInserted(position)
    }

    fun setItem(model: MODEL, position: Int) {
        itemList[position] = model
        notifyItemChanged(position, model)
    }

    fun removeItemAt(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearItems(){
        itemList.clear()
        notifyDataSetChanged()
    }

    fun getItems() = itemList

    class Builder<Model, Vh : BaseViewHolder<Model>>(
        @LayoutRes
        val layoutId: Int,
        val itemList: MutableList<Model>,
        val vhClass: Class<Vh>,
        val bundle: Bundle,
        val listener: BaseListener
    ) {
        fun build() = BasicRecyclerViewAdapter(this)
    }

    interface BaseListener {}

    abstract class BaseViewHolder<MODEL>(b: ViewDataBinding) : RecyclerView.ViewHolder(b.root) {
        var binding = b
        abstract fun onBindViewHolder(model: MODEL, bundle: Bundle, position:Int, listener: BaseListener)
    }
}