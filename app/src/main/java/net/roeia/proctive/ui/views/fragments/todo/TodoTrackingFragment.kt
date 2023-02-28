package net.roeia.proctive.ui.views.fragments.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.yesserly.resto.models.pojo.Option
import dagger.hilt.android.AndroidEntryPoint
import net.roeia.proctive.R
import net.roeia.proctive.databinding.FragmentTodoTrackingBinding
import net.roeia.proctive.ui.viewmodels.todo.TodoTrackingViewModel
import net.roeia.proctive.utils.adapters.CHIP
import net.roeia.proctive.utils.adapters.OptionsAdapter

@AndroidEntryPoint
class TodoTrackingFragment : Fragment() {
    companion object {
        private const val TAG = "ManageTodoFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: TodoTrackingViewModel by viewModels()
    private var _binding: FragmentTodoTrackingBinding? = null
    private val binding get() = _binding!!

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Init Graph Styling
        initMeasurementGraphStyling()

        //Init Tracking Options
        initTrackingOptions()

        //Init RecyclerView
        initTrendRecyclerView()

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
    private fun initMeasurementGraphStyling() {
        binding.trackings.axisRight.isEnabled = false
        binding.trackings.description.isEnabled = false
        binding.trackings.legend.isEnabled = false
        binding.trackings.extraLeftOffset = 4F
        //binding.trackings.axisLeft.valueFormatter = MeasurementAxisCustomFormatter(false)
        binding.trackings.xAxis.setDrawGridLines(false)
        binding.trackings.xAxis.textColor = resources.getColor(R.color.white, null)
        binding.trackings.axisLeft.textColor = resources.getColor(R.color.white, null)
        binding.trackings.xAxis.position = XAxis.XAxisPosition.BOTTOM
        /*
        binding.trackings.xAxis.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.integralcf_medium)
        binding.trackings.axisLeft.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.integralcf_medium)*/
    }

    private fun initTrackingOptions() {
        binding.graphOptions.setAdapter(
            OptionsAdapter.Builder()
                .setOptions(resources.getStringArray(R.array.task_tracking).map { Option(it) })
                .setType(CHIP)
                .setOnSelectedListener(object : OptionsAdapter.OnSelectedListener {
                    override fun onSelected(position: Int, value: String?) {
                        //viewModel.currentType = value!!
                        //viewModel.getTracks(value)
                    }
                }).build()
        )
    }

    private fun initTrackingGraph(entries: List<Entry>, labels: List<String>) {
        //binding.trackings.xAxis.valueFormatter = MeasurementAxisCustomFormatter(true, labels)

        //Set Data
        if (binding.trackings.data != null && binding.trackings.data.dataSetCount > 0) {
            (binding.trackings.data.getDataSetByIndex(0) as LineDataSet).values = entries
            binding.trackings.data.notifyDataChanged()
            binding.trackings.notifyDataSetChanged()
        } else {
            val set = LineDataSet(entries, "")
            initGraphSetStyling(set)
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set)
            val data = LineData(dataSets)
            binding.trackings.data = data
        }
    }

    private fun initGraphSetStyling(set: LineDataSet) {
        set.color = resources.getColor(R.color.teal_200, null)
        set.setGradientColor(
            resources.getColor(R.color.teal_200, null),
            resources.getColor(android.R.color.transparent, null)
        )
        //val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.graph_fade)
        //set.fillDrawable = drawable
        set.setDrawFilled(true)

        //Circle Points
        set.setCircleColor(resources.getColor(R.color.white, null))
        set.lineWidth = 2f
        set.circleRadius = 4f
        set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        set.setDrawCircleHole(false)

        set.setDrawHorizontalHighlightIndicator(false)
        set.setDrawVerticalHighlightIndicator(false)

        //Text
        set.setDrawValues(false)
    }

    private fun initTrendRecyclerView() {

    }

    private fun initClickListeners() {
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}