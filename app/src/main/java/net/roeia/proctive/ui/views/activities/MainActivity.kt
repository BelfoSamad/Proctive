package net.roeia.proctive.ui.views.activities

import android.content.Context
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.roeia.proctive.R

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun goTodo(view: View) {
        val bundle = Bundle()
        bundle.putInt("PAGE_TYPE", view.tag.toString().toInt())
        findNavController(R.id.nav_host_fragment).navigate(R.id.go_todo, bundle)
    }

    fun goHabits(view: View) {
        findNavController(R.id.nav_host_fragment).navigate(R.id.go_habit)
    }

    fun goFinance(view: View) {
        val bundle = Bundle()
        bundle.putInt("PAGE_TYPE", view.tag.toString().toInt())
        findNavController(R.id.nav_host_fragment).navigate(R.id.go_finance, bundle)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}