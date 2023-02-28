package net.roeia.proctive.utils.adapters

import com.yesserly.resto.models.pojo.Option

const val TEXT: Int = 1
const val BUTTON: Int = 2
const val CHIP: Int = 3

class OptionsAdapter private constructor(
    val options: List<Option>?,
    val type: Int?,
    val multiSelect: Boolean? = false,
    val listener: OnSelectedListener?
) {

    interface OnSelectedListener {
        fun onSelected(position: Int, value: String?) {
            //Default
        }

        fun onMultiSelected(positions: List<Int>, values: List<String>) {
            //Default
        }
    }

    data class Builder(
        var options: List<Option>? = null,
        var type: Int? = null,
        var multiSelect: Boolean? = false,
        var listener: OnSelectedListener? = null
    ) {
        fun setOptions(options: List<Option>) = apply { this.options = options }
        fun setType(type: Int) = apply { this.type = type }
        fun setMultiSelect(multiSelect: Boolean) = apply { this.multiSelect = multiSelect }
        fun setOnSelectedListener(listener: OnSelectedListener) = apply { this.listener = listener }
        fun build() = OptionsAdapter(options, type, multiSelect, listener)
    }
}