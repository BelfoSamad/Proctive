package net.roeia.proctive.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.InvocationTargetException

class BaseMultiPagerAdapter<VH : BaseViewHolder<Any>> private constructor(
    @LayoutRes
    private val layoutIds: List<Int>,
    private val vhClasses: List<Class<VH>>,
    private val itemList: List<Any?>,
    private var bundles: List<Bundle>,
    private val listeners: List<BaseViewHolder.BaseListener?>
) : RecyclerView.Adapter<BaseViewHolder<Any>>() {

    /***********************************************************************************************
     * ************************* Constructor
     */
    private constructor(builder: Builder<VH>) : this(
        builder.layoutIds,
        builder.vhClasses,
        builder.itemList,
        builder.bundles,
        builder.listeners
    )

    /***********************************************************************************************
     * ************************* Base Methods
     */
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Any> {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layoutIds[viewType],
            parent,
            false
        )

        var vh: VH? = null
        try {
            vh = vhClasses[viewType].constructors[0].newInstance(binding) as VH
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

    override fun onBindViewHolder(holder: BaseViewHolder<Any>, position: Int) {
        holder.onBindViewHolder(itemList[position], bundles[position], position, listeners[position])
    }

    override fun getItemCount(): Int = vhClasses.size

    /***********************************************************************************************
     * ************************* Builder
     */
    class Builder<Vh : BaseViewHolder<Any>>(
        @LayoutRes
        val layoutIds: List<Int>,
        val vhClasses: List<Class<Vh>>,
        val itemList: List<Any?>,
        val bundles: List<Bundle>,
        val listeners: List<BaseViewHolder.BaseListener?>
    ) {
        fun build() = BaseMultiPagerAdapter(this)
    }
}