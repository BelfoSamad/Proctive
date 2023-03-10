package net.roeia.proctive.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.lang.reflect.InvocationTargetException

class BaseBottomSheet<MODEL, VH : BaseViewHolder<MODEL>> private constructor(
    @LayoutRes
    private val layoutId: Int,
    private val item: MODEL?,
    private val vhClass: Class<VH>,
    private var bundle: Bundle,
    private val listener: BaseViewHolder.BaseListener?
) : BottomSheetDialogFragment() {

    var vh: VH? = null

    /***********************************************************************************************
     * ************************* Constructor
     */
    private constructor(builder: Builder<MODEL, VH>) : this(
        builder.layoutId,
        builder.item,
        builder.vhClass,
        builder.bundle,
        builder.listener
    )

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(inflater.context),
            layoutId,
            container,
            false
        )
        try {
            vh = vhClass.constructors[0].newInstance(binding) as VH
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: java.lang.InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }

        return if (vh == null) {
            throw NullPointerException()
        } else {
            vh!!.itemView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vh?.onBindViewHolder(item, bundle, -1, listener)
    }

    /***********************************************************************************************
     * ************************* Builder
     */
    class Builder<Model, Vh : BaseViewHolder<Model>>(
        @LayoutRes
        val layoutId: Int,
        val item: Model?,
        val vhClass: Class<Vh>,
        val bundle: Bundle,
        val listener: BaseViewHolder.BaseListener?
    ) {
        fun build() = BaseBottomSheet(this)
    }
}