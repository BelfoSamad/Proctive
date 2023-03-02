package net.roeia.proctive.ui.custom

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import net.roeia.proctive.R


class TooltipWindow(context: Context) {

    val rootView: View
    val tipWindow: PopupWindow

    init {
        val inflater = LayoutInflater.from(context)
        rootView = inflater.inflate(R.layout.tooltip_sort, null).apply {
            measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
        }
        tipWindow = PopupWindow(context)
    }

    fun PopupWindow.dimBehind() {
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
        //tipWindow.setBackgroundDrawable(BitmapDrawable())

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

}