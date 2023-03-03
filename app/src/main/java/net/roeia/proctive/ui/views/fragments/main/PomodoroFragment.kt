package net.roeia.proctive.ui.views.fragments.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.view.View.OnTouchListener
import android.widget.HorizontalScrollView
import android.widget.ListView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import net.roeia.proctive.R
import net.roeia.proctive.databinding.BottomSheetTaskBinding
import net.roeia.proctive.databinding.FragmentPomodoroBinding
import net.roeia.proctive.databinding.PagePomodoroAverageBinding
import net.roeia.proctive.models.entities.Todo
import net.roeia.proctive.models.enums.TodoType
import net.roeia.proctive.ui.viewmodels.main.PomodoroViewModel
import net.roeia.proctive.utils.adapters.PagerAdapter
import net.roeia.proctive.utils.adapters.SubTasksArrayAdapter
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

    //Bind Listener
    private val bindListener = object : PagerAdapter.BindListener {
        override fun bind(holder: PagePomodoroAverageBinding) {
            holder.averagePomodoros.text = "12 Pomodoro"
            holder.bestPomodoros.text = "12 Pomodoro"
            holder.backWeek.setOnClickListener {
                //TODO: Back Week
            }
            holder.forwardWeek.setOnClickListener {
                //TODO: Forward Week
            }
        }

        override fun bind(holder: BottomSheetTaskBinding) {
            val todo = Todo(
                todoId = 1L,
                name = "Publish Proctive",
                labels = listOf("Android", "Application"),
                description = "This is the description of the goal with the title Publish Proctive.",
                type = TodoType.Goal,
                isChecked = false,
                due = Date(),
                pomodoroAverage = 5,
                subTasks = mapOf(
                    "Finish UI" to true,
                    "Finish Backend" to false,
                    "Publishing Preparations" to true
                ),
                goalRef = 112133443L
            )
            holder.deleteTodo.visibility = View.GONE
            holder.editTask.visibility = View.GONE
            holder.todoDescription.text = todo.description
            (holder.subTasks as ListView).adapter =
                SubTasksArrayAdapter(
                    requireContext(),
                    todo.subTasks!!.toMutableMap(),
                    true,
                    object : SubTasksArrayAdapter.SubTaskActions {
                        override fun onCheckSubTask(subtask: String, checked: Boolean) {

                        }
                    }
                )

            //Init Labels List
            for (label in todo.labels!!) {
                val chip = HorizontalScrollView.inflate(
                    requireContext(),
                    R.layout.single_chip_option,
                    null
                ) as Chip
                chip.text = label
                chip.setTextColor(resources.getColor(R.color.paper_300, null))
                chip.setChipBackgroundColorResource(R.color.black)
                holder.labels.addView(chip)
            }

            //Colors
            holder.root.backgroundTintList =
                AppCompatResources.getColorStateList(requireContext(), R.color.paper_500)
        }
    }

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
        binding.viewpager.adapter = PagerAdapter(bindListener)
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