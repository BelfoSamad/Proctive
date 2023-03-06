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
import net.roeia.proctive.databinding.BottomSheetTaskBinding
import net.roeia.proctive.databinding.FragmentPomodoroBinding
import net.roeia.proctive.databinding.PagePomodoroAverageBinding
import net.roeia.proctive.models.entities.todo.Todo
import net.roeia.proctive.models.enums.TodoType
import net.roeia.proctive.models.pojo.TodoChecked
import net.roeia.proctive.ui.viewmodels.main.PomodoroViewModel
import net.roeia.proctive.ui.views.viewholders.listviews.SubTasksViewHolder
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

            //Pomodoro Tracking
            initMeasurementGraphStyling(holder.average)
            val entries = listOf(
                BarEntry(0f, 40f),
                BarEntry(1f, 50f),
                BarEntry(2f, 60f),
                BarEntry(3f, 20f),
                BarEntry(4f, 30f),
            )
            if (holder.average.data != null && holder.average.data.dataSetCount > 0) {
                (holder.average.data.getDataSetByIndex(0) as LineDataSet).values = entries
                holder.average.data.notifyDataChanged()
                holder.average.notifyDataSetChanged()
            } else {
                val set = BarDataSet(entries, "")
                initGraphSetStyling(set)
                val dataSets: ArrayList<IBarDataSet> = ArrayList()
                dataSets.add(set)
                val data = BarData(dataSets)
                holder.average.data = data
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

            //Update ListView
            val bundle = Bundle()
            bundle.putBoolean("isChecked", true)
            val adapter = BaseArrayAdapter.Builder(
                itemsList = todo.subTasks!!.map { TodoChecked(it.key, it.value) }.toMutableList(),
                layoutId = R.layout.list_task_item,
                vhClass = SubTasksViewHolder::class.java,
                bundle = bundle,
                listener = object : SubTasksViewHolder.SubTaskActions {
                    override fun onCheckSubTask(subtask: String, checked: Boolean) {

                    }
                }
            ).build()
            (holder.subTasks as ListView).adapter = adapter

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
    private fun initMeasurementGraphStyling(barChart: BarChart) {
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.extraLeftOffset = 4F
        barChart.xAxis.valueFormatter = DaysAxisFormatter(resources.getStringArray(R.array.months).toList())
        barChart.xAxis.setDrawGridLines(false)
        barChart.xAxis.textColor = resources.getColor(R.color.black, null)
        barChart.axisLeft.textColor = resources.getColor(R.color.black, null)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.typeface = ResourcesCompat.getFont(requireContext(), R.font.lufga_medium)
        barChart.axisLeft.typeface = ResourcesCompat.getFont(requireContext(), R.font.lufga_medium)
    }

    private fun initGraphSetStyling(set: BarDataSet) {
        set.color = resources.getColor(R.color.black, null)
        set.valueTextColor = resources.getColor(R.color.black, null)
        set.setDrawValues(false)
    }

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