package net.roeia.proctive.ui.views.fragments.finance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import net.roeia.proctive.R
import net.roeia.proctive.databinding.FragmentManageIncomeBinding
import net.roeia.proctive.models.enums.FinanceType
import net.roeia.proctive.models.pojo.Finance
import net.roeia.proctive.ui.views.viewholders.dialogs.AddSplittingViewHolder
import net.roeia.proctive.ui.views.viewholders.dialogs.ManageExpenditureViewHolder
import net.roeia.proctive.ui.views.viewholders.recyclerviews.FinanceViewHolder
import net.roeia.proctive.utils.BaseDialog
import net.roeia.proctive.utils.BaseViewHolder
import net.roeia.proctive.utils.BasicRecyclerViewAdapter

@AndroidEntryPoint
class ManageIncomeFragment : Fragment() {
    companion object {
        private const val TAG = "ManageIncomeFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: ManageIncomeViewModel by viewModels()
    private var _binding: FragmentManageIncomeBinding? = null
    private val binding get() = _binding!!

    //Data
    var adapter: BasicRecyclerViewAdapter<Finance, FinanceViewHolder>? = null
    var isSavings: Boolean? = null

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isSavings = requireArguments().getBoolean("IsSavings")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageIncomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set View Data
        binding.isSavings = isSavings

        //Init RecyclerView

        initRecyclerView(
            if (isSavings!!) listOf()
            else listOf(
                Finance(
                    "To Saving",
                    "All remaining",
                    "10000",
                    null
                )
            )
        )

        //Init Interval Unit
        initIntervalSpinner()

        //Init ClickListeners
        initClickListener()
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    private fun initRecyclerView(finances: List<Finance>) {
        val bundle = Bundle()
        if (isSavings!!)
            bundle.putInt("PageType", FinanceType.Savings.ordinal)
        else bundle.putInt("PageType", FinanceType.Income.ordinal)
        adapter = BasicRecyclerViewAdapter.Builder(
            itemList = finances.toMutableList(),
            layoutId = R.layout.recyclerview_finance_item,
            vhClass = FinanceViewHolder::class.java,
            bundle = bundle,
            listener = null
        ).build()
        binding.splittingList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.splittingList.adapter = adapter
    }

    private fun initIntervalSpinner() {
        ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            resources.getStringArray(R.array.months).map { it }
        ).also { adapter ->
            binding.intervalUnit.adapter = adapter
            binding.intervalUnit.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        //Set Activity level on ViewModel
                        //viewModel.setExperience(Experience.values().map { it.name }[p2])
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        //Do Nothing
                    }
                }
        }
    }

    private fun initClickListener() {
        //Back
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        //Add Splitting
        binding.addSplitting.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isSavings", isSavings!!)
            val dialog = BaseDialog.Builder(
                layoutId = R.layout.dialog_add_splitting,
                item = null,
                vhClass = AddSplittingViewHolder::class.java,
                listener = object : AddSplittingViewHolder.SplittingListener {
                    override fun onSaveSplitting(
                        isPercentage: Boolean,
                        financeType: FinanceType,
                        amount: Double
                    ) {
                        adapter?.addItem(
                            Finance(
                                "To ${resources.getStringArray(R.array.finance_title)[financeType.ordinal]}",
                                null,
                                if (isPercentage) "$amount%" else "${amount}da",
                                null
                            )
                        )
                    }
                },
                bundle = bundle
            ).build()
            dialog.show(childFragmentManager, "AddSplittingDialog")
        }
    }


}