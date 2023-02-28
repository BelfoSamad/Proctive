package net.roeia.proctive.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView


class UnScrollableListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ListView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMeasureSpecCustom =
            MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, heightMeasureSpecCustom)
        val params = layoutParams
        params.height = measuredHeight
    }
}