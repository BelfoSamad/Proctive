package net.roeia.proctive.utils

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import net.roeia.proctive.ui.views.viewholders.dialogs.AddSplittingViewHolder
import java.lang.reflect.InvocationTargetException

class BaseDialog<MODEL, VH : BaseViewHolder<MODEL>> private constructor(
    @LayoutRes
    private val layoutId: Int,
    private val item: MODEL?,
    private val vhClass: Class<VH>,
    private var bundle: Bundle,
    private val listener: BaseViewHolder.BaseListener?
) : DialogFragment() {

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
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            val binding = DataBindingUtil.inflate<ViewDataBinding>(
                inflater,
                layoutId,
                null,
                false
            )
            var vh: VH? = null
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

            if (vh == null) {
                throw NullPointerException()
            } else {
                vh.onBindViewHolder(item, bundle, 0, listener)
            }

            val dialog = builder.setView(binding.root).create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
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
        fun build() = BaseDialog(this)
    }
}