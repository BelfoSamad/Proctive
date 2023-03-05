package net.roeia.proctive.utils

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.InvocationTargetException

class BasicRecyclerViewAdapter<MODEL, VH : BaseViewHolder<MODEL>> private constructor(
    @LayoutRes
    private val layoutId: Int,
    private val itemList: MutableList<MODEL>,
    private val vhClass: Class<VH>,
    private var bundle: Bundle,
    private val listener: BaseViewHolder.BaseListener?
) : RecyclerView.Adapter<BaseViewHolder<MODEL>>() {

    /***********************************************************************************************
     * ************************* Constructor
     */
    private constructor(builder: Builder<MODEL, VH>) : this(
        builder.layoutId,
        builder.itemList,
        builder.vhClass,
        builder.bundle,
        builder.listener
    )


    /***********************************************************************************************
     * ************************* Base Methods
     */
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

    /***********************************************************************************************
     * ************************* Methods
     */
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

    fun removeItem(todo: MODEL) {
        val position = itemList.indexOf(todo)
        itemList.remove(todo)
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearItems() {
        itemList.clear()
        notifyDataSetChanged()
    }

    fun getItems() = itemList

    @SuppressLint("NotifyDataSetChanged")
    fun setBundle(bundle: Bundle) {
        this.bundle = bundle
        notifyDataSetChanged()
    }

    /***********************************************************************************************
     * ************************* Builder
     */
    class Builder<Model, Vh : BaseViewHolder<Model>>(
        @LayoutRes
        val layoutId: Int,
        val itemList: MutableList<Model>,
        val vhClass: Class<Vh>,
        val bundle: Bundle,
        val listener: BaseViewHolder.BaseListener?
    ) {
        fun build() = BasicRecyclerViewAdapter(this)
    }
}