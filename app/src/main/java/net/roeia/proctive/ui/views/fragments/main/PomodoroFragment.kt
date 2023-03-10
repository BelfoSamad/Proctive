package net.roeia.proctive.ui.views.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.HorizontalScrollView
import android.widget.ListView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import net.roeia.proctive.R
import net.roeia.proctive.base.ui.BaseArrayAdapter
import net.roeia.proctive.base.ui.BaseMultiPagerAdapter
import net.roeia.proctive.databinding.BottomSheetTaskBinding
import net.roeia.proctive.databinding.FragmentPomodoroBinding
import net.roeia.proctive.databinding.PagePomodoroAverageBinding
import net.roeia.proctive.models.entities.todo.Todo
import net.roeia.proctive.models.enums.TodoType
import net.roeia.proctive.models.pojo.TodoChecked
import net.roeia.proctive.ui.viewmodels.main.PomodoroViewModel
import net.roeia.proctive.ui.views.viewholders.bottomsheets.BottomTodoViewHolder
import net.roeia.proctive.ui.views.viewholders.listviews.SubTasksViewHolder
import net.roeia.proctive.ui.views.viewholders.viewpagers.PomodoroAverageViewHolder
import net.roeia.proctive.utils.DaysAxisFormatter
import net.roeia.proctive.utils.adapters.PagerAdapter
import java.util.*


@AndroidEntryPoint
class PomodoroFragment : Fragment(), GestureDetector.OnGestureListener {
    companion object {
        private const val TAG = "PomodoroFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: PomodoroViewModel by viewModels()
    private var _binding: FragmentPomodoroBinding? = null
    private val binding get() = _binding!!

    var isRest = false
    var timerRemaining: Long = 1500000
    var timer: CountDownTimer? = null

    //Swipe Detection
    private lateinit var gestureDetector: GestureDetector
    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPomodoroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gestureDetector = GestureDetector(requireContext(), this)

        //Init ViewPager
        initViewPager()

        //Init ClickListeners
        initClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    private fun initViewPager() {
        val adapter = BaseMultiPagerAdapter.Builder(
            layoutIds = listOf(
                R.layout.page_pomodoro_average,
                R.layout.page_pomodoro_average
            ),
            vhClasses = listOf(
                PomodoroAverageViewHolder::class.java,
                PomodoroAverageViewHolder::class.java
            ),
            bundles = listOf(
                Bundle(),
                Bundle()
            ),
            itemList = listOf("Test", null),
            listeners = listOf(null, null)
        ).build()
        binding.viewpager.adapter = adapter
        val titles = listOf("Tasks", "Average")
        TabLayoutMediator(binding.tablayout, binding.viewpager) { tab: TabLayout.Tab, i: Int ->
            tab.text = titles[i]
        }.attach()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initClickListeners() {
        //Back
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        //Settings
        binding.settings.setOnClickListener {
            //TODO: Open Settings Dialog
        }

        //Timer
        binding.pomodoroTimer.setOnClickListener {
            if (it.tag == 1) {
                timer?.cancel()
                it.tag = 0
            } else {
                startTimer(timerRemaining)
                it.tag = 1
            }
        }
        binding.pomodoroTimer.setOnTouchListener { _, motionEvent ->
            gestureDetector.onTouchEvent(motionEvent!!)
        }
    }

    private fun startTimer(remaining: Long) {
        timer = object : CountDownTimer(remaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerRemaining = millisUntilFinished
                val seconds = millisUntilFinished / 1000 % 60
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                binding.pomodoroTimerText.text =
                    "${reformatTime(minutes.toInt())}:${reformatTime(seconds.toInt())}"
            }

            override fun onFinish() {
                TODO("Not yet implemented")
            }
        }.start()
    }

    fun reformatTime(time: Int): String {
        return if (time < 10)
            "0$time";
        else time.toString()
    }

    override fun onDown(p0: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(p0: MotionEvent) {
        return
    }

    override fun onSingleTapUp(p0: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean {
        return false
    }

    override fun onLongPress(p0: MotionEvent) {
        return
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        try {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            if (kotlin.math.abs(diffX) < kotlin.math.abs(diffY)) {
                if (kotlin.math.abs(diffY) > swipeThreshold && kotlin.math.abs(velocityX) > swipeVelocityThreshold) {
                    //to change animation if (diffY > 0) { top to bottom else bottom to top }
                    isRest = !isRest
                    if (isRest) {
                        binding.pomodoroTimerText.text = "05:00"
                        binding.pomodoroTimerText.setTextColor(
                            resources.getColor(
                                R.color.green_300,
                                null
                            )
                        )
                        binding.pomodoroSubtimerText.text = "25:00"
                        timer?.cancel()
                        timerRemaining = 300000
                    } else {
                        binding.pomodoroTimerText.text = "25:00"
                        binding.pomodoroTimerText.setTextColor(
                            resources.getColor(
                                R.color.red_300,
                                null
                            )
                        )
                        binding.pomodoroSubtimerText.text = "05:00"
                        timer?.cancel()
                        timerRemaining = 1500000
                    }
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return true
    }
}