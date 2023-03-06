package net.roeia.proctive.base.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import java.lang.reflect.InvocationTargetException

class BaseArrayAdapter<MODEL, VH : BaseViewHolder<MODEL>> private constructor(
    @LayoutRes
    private val layoutId: Int,
    private val itemsList: MutableList<MODEL>,
    private val vhClass: Class<VH>,
    private var bundle: Bundle,
    private val listener: BaseViewHolder.BaseListener?
)  : BaseAdapter()  {

    /***********************************************************************************************
     * ************************* Constructor
     */
    private constructor(builder: Builder<MODEL, VH>) : this(
        builder.layoutId,
        builder.itemsList,
        builder.vhClass,
        builder.bundle,
        builder.listener
    )

    /***********************************************************************************************
     * ************************* Methods
     */
    override fun getCount() = itemsList.size

    override fun getItem(position: Int): MODEL = itemsList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    fun removeItem(item: MODEL){
        itemsList.remove(item)
        notifyDataSetChanged()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent?.context),
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
            vh.onBindViewHolder(itemsList[position], bundle, position, listener)
            vh.itemView
        }
    }

    /***********************************************************************************************
     * ************************* Builder
     */
    class Builder<Model, Vh : BaseViewHolder<Model>>(
        @LayoutRes
        val layoutId: Int,
        val itemsList: MutableList<Model>,
        val vhClass: Class<Vh>,
        val bundle: Bundle,
        val listener: BaseViewHolder.BaseListener?
    ) {
        fun build() = BaseArrayAdapter(this)
    }
}