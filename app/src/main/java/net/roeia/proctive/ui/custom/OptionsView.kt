package net.roeia.proctive.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView
import com.yesserly.resto.models.pojo.Option
import net.roeia.proctive.R

const val TEXT: Int = 1
const val BUTTON: Int = 2
const val CHIP: Int = 3
class OptionsView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    HorizontalScrollView(context, attrs) {

    /***********************************************************************************************
     * ************************* Declarations
     */
    private lateinit var adapter: OptionsAdapter
    private val container: LinearLayout

    private var optionsBackgroundColor: Int
    private var optionsForegroundColor: Int

    /***********************************************************************************************
     * ************************* Init
     */
    init {
        val rootView: View = inflate(context, R.layout.custom_options, this)
        container = rootView.findViewById(R.id.options_container)
        context.theme.obtainStyledAttributes(attrs, R.styleable.OptionsView, 0, 0).apply {
            try {
                optionsBackgroundColor =
                    getResourceId(R.styleable.OptionsView_optionBackgroundColor, -1)
                optionsForegroundColor =
                    getResourceId(R.styleable.OptionsView_optionForegroundColor, -1)
            } finally {
                recycle()
            }
        }
    }

    fun setAdapter(adapter: OptionsAdapter) {
        this.adapter = adapter
        initUI()
    }

    fun getAdapter() = adapter

    @SuppressLint("ResourceType")
    private fun initUI() {
        when (adapter.type) {
            TEXT -> {
                val params = LayoutParams(
                    350,
                    350
                )
                params.setMargins(16, top, 16, bottom)
                for ((index, option) in adapter.options!!.withIndex()) {
                    val text = inflate(
                        context,
                        R.layout.single_text_option,
                        null
                    ) as MaterialTextView
                    text.text = option.name
                    text.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        ResourcesCompat.getDrawable(resources, R.drawable.holder, null),
                        null,
                        null
                    )
                    text.setOnClickListener {
                        //Deselect Others
                        unselectAll()
                        //Select View
                        text.isSelected = true
                        TextViewCompat.setCompoundDrawableTintList(
                            text,
                            ColorStateList.valueOf(resources.getColor(R.color.black, null))
                        )
                        text.setTextColor(ContextCompat.getColor(context, R.color.black))
                        //Return Data
                        adapter.listener?.onSelected(index, option.name)
                    }
                    container.addView(text, params)
                }
                unselectAll()
            }
            BUTTON -> {
                val params = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                )
                params.setMargins(16, top, 16, bottom)
                for ((index, option) in adapter.options!!.withIndex()) {
                    val button = MaterialButton(context)
                    button.text = option.name
                    button.setOnClickListener {
                        //Return Data
                        adapter.listener?.onSelected(index, option.name)
                    }
                    button.layoutParams = params
                    container.addView(button)
                }
            }
            CHIP -> {
                val chipGroup = ChipGroup(context)
                for (option in adapter.options!!) {
                    val chip = inflate(
                        context,
                        R.layout.single_chip_option,
                        null
                    ) as Chip
                    chip.text = option.name
                    chip.isCheckable = true
                    if (optionsBackgroundColor != -1)
                        chip.setChipBackgroundColorResource(optionsBackgroundColor)
                    if (optionsForegroundColor != -1)
                        chip.setTextColor(resources.getColor(optionsForegroundColor, null))
                    chipGroup.addView(chip)
                }
                chipGroup.isSingleSelection = !adapter.multiSelect!!
                chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                    val values = mutableMapOf<Int, String>()
                    for (id in checkedIds) {
                        val pos = group.indexOfChild(group.findViewById(id))
                        values[pos] = adapter.options!![pos].name
                    }

                    if (values.isEmpty()) {
                        adapter.listener?.onSelected(null, null)
                        adapter.listener?.onMultiSelected(null, null)
                    } else {
                        adapter.listener?.onSelected(
                            values.keys.elementAt(0),
                            values.values.elementAt(0)
                        )
                        adapter.listener?.onMultiSelected(
                            values.keys.toList(),
                            values.values.toList()
                        )
                    }

                }
                container.addView(chipGroup)
            }
        }
    }

    private fun unselectAll() {
        for (i in 0 until container.childCount) {
            when (adapter.type) {
                CHIP -> {
                    //Deselect Text
                    val text = container.getChildAt(i) as MaterialTextView
                    text.isSelected = false
                    TextViewCompat.setCompoundDrawableTintList(
                        text,
                        ColorStateList.valueOf(resources.getColor(R.color.white, null))
                    )
                    text.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
            }
        }
    }

    fun selectAt(index: Int) {
        unselectAll()
        when (adapter.type) {
            TEXT -> {
                //Deselect Text
                val text = container.getChildAt(index) as MaterialTextView
                text.isSelected = true
                TextViewCompat.setCompoundDrawableTintList(
                    text,
                    ColorStateList.valueOf(resources.getColor(R.color.black, null))
                )
                text.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            BUTTON -> {
            }
            CHIP -> {
                val chip = container.getChildAt(0) as ChipGroup
                (chip.getChildAt(index) as Chip).isSelected = true
            }
        }
    }

    /***********************************************************************************************
     * ************************* Adapter
     */
    class OptionsAdapter private constructor(
        val options: List<Option>?,
        val type: Int?,
        val multiSelect: Boolean? = false,
        val listener: OnSelectedListener?
    ) {

        interface OnSelectedListener {
            fun onSelected(position: Int?, value: String?) {
                //Default
            }

            fun onMultiSelected(positions: List<Int>?, values: List<String>?) {
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
}