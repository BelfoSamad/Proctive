package net.roeia.proctive.ui.custom

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import net.roeia.proctive.R

class LabeledInputView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val input: TextInputLayout
    private val addInput: ImageButton
    private var labelIcon: Int
    private var inputBackgroundColor: Int
    private var labelText: String?
    private var hint: String?
    private var inputStyle: Int
    private var inputType: Int
    private val container: LinearLayout

    interface OnAddListener {

        fun onValueAdded(value: String)

    }

    /***********************************************************************************************
     * ************************* Init
     */
    init {
        val rootView: View = inflate(context, R.layout.custom_labeled_input, this)
        container = rootView.findViewById(R.id.input_container)
        context.theme.obtainStyledAttributes(attrs, R.styleable.LabeledInputView, 0, 0).apply {
            try {
                labelIcon = getResourceId(R.styleable.LabeledInputView_labelIcon, 0)
                labelText = getString(R.styleable.LabeledInputView_labelText)
                hint = getString(R.styleable.LabeledInputView_hint)
                inputStyle = getResourceId(R.styleable.LabeledInputView_inputStyle, 0)
                inputType = getInteger(R.styleable.LabeledInputView_inputType, -1)
                inputBackgroundColor = getInteger(R.styleable.LabeledInputView_inputBackgroundColor, 0)
            } finally {
                recycle()
            }
        }

        //Init UI
        val label = rootView.findViewById<MaterialTextView>(R.id.input_label)
        input = rootView.findViewById(R.id.input)
        addInput = rootView.findViewById(R.id.add_value)
        label.text = labelText
        label.setCompoundDrawablesWithIntrinsicBounds(
            ResourcesCompat.getDrawable(resources, labelIcon, null),
            null,
            null,
            null
        )
        input.hint = hint
        input.boxBackgroundColor = inputBackgroundColor
        when (inputType) {
            0 -> input.editText?.inputType = InputType.TYPE_CLASS_TEXT
            1 -> input.editText?.inputType = InputType.TYPE_CLASS_NUMBER
            2 -> {
                input.editText?.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
                input.editText?.setLines(5)
                input.editText?.gravity = Gravity.TOP
            }
            3 -> {
                input.editText?.inputType = InputType.TYPE_CLASS_TEXT
                addInput.visibility = VISIBLE
            }
        }
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    fun setOnAddListener(listener: OnAddListener) {
        addInput.setOnClickListener {
            if (isValid()) {
                listener.onValueAdded(input.editText?.text.toString())
                input.editText?.setText("")
                input.editText?.clearFocus()
            }
        }
    }

    fun setInputBackgroundColor(color: Int) {
        input.boxBackgroundColor = color
        invalidate()
        requestLayout()
    }

    fun isValid(): Boolean {
        input.error = null
        return if (input.editText?.text.toString() == "") {
            input.error = "$labelText Shouldn't be empty"
            false
        } else true
    }

    fun setValue(value: String) {
        input.editText?.setText(value)
    }

    fun getValue() = input.editText?.text.toString()
}