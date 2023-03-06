package net.roeia.proctive.base.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import java.lang.reflect.InvocationTargetException

class BaseTooltip<MODEL, VH : BaseViewHolder<MODEL>> private constructor(
    @LayoutRes
    private val layoutId: Int,
    private val item: MODEL?,
    private val vhClass: Class<VH>,
    private var bundle: Bundle,
    private val listener: BaseViewHolder.BaseListener?
){

    /***********************************************************************************************
     * ************************* Declarations
     */
    private lateinit var rootView: View
    private lateinit var tipWindow: PopupWindow

    /***********************************************************************************************
     * ************************* Constructor
     */
    private constructor(builder: Builder<MODEL, VH>) : this(
        builder.layoutId,
        builder.item,
        builder.vhClass,
        builder.bundle,
        builder.listener
    ) {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(builder.context),
            layoutId,
            null,
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

        if (vh == null) {
            throw NullPointerException()
        } else {
            tipWindow = PopupWindow(builder.context)
            rootView = vh.itemView
            vh.onBindViewHolder(item, bundle, -1, listener)
        }
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    private fun PopupWindow.dimBehind() {
        val container = contentView.rootView
        val context = contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.5f
        wm.updateViewLayout(container, p)
    }

    fun showTooltip(anchor: View) {
        tipWindow.height = LinearLayout.LayoutParams.WRAP_CONTENT
        tipWindow.width = LinearLayout.LayoutParams.WRAP_CONTENT
        tipWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        tipWindow.isOutsideTouchable = true
        tipWindow.isTouchable = true
        tipWindow.isFocusable = true
        tipWindow.contentView = rootView

        val location = IntArray(2).apply {
            anchor.getLocationOnScreen(this)
        }
        val size = Size(
            tipWindow.contentView.measuredWidth,
            tipWindow.contentView.measuredHeight
        )

        tipWindow.showAtLocation(
            anchor,
            Gravity.NO_GRAVITY,
            location[0] - (size.width / 2) - 10,
            location[1] + anchor.height
        )
        tipWindow.dimBehind()
    }

    fun isTooltipShown(): Boolean {
        if (tipWindow.isShowing) return true
        return false
    }

    fun dismissTooltip() {
        if (tipWindow.isShowing) tipWindow.dismiss()
    }

    /***********************************************************************************************
     * ************************* Builder
     */
    class Builder<Model, Vh : BaseViewHolder<Model>>(
        val context: Context,
        @LayoutRes
        val layoutId: Int,
        val item: Model?,
        val vhClass: Class<Vh>,
        val bundle: Bundle,
        val listener: BaseViewHolder.BaseListener?
    ) {
        fun build() = BaseTooltip(this)
    }
}