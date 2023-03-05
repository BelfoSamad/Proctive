package net.roeia.proctive.ui.views.fragments.finance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.roeia.proctive.R
import net.roeia.proctive.data.Status
import net.roeia.proctive.databinding.FragmentFinanceBinding
import net.roeia.proctive.models.entities.Expenditure
import net.roeia.proctive.models.enums.FinanceType
import net.roeia.proctive.models.pojo.Finance
import net.roeia.proctive.ui.viewmodels.finance.FinanceViewModel
import net.roeia.proctive.ui.views.viewholders.dialogs.ManageDebtViewHolder
import net.roeia.proctive.ui.views.viewholders.dialogs.ManageExpenditureViewHolder
import net.roeia.proctive.ui.views.viewholders.recyclerviews.FinanceViewHolder
import net.roeia.proctive.utils.BaseDialog
import net.roeia.proctive.utils.BasicRecyclerViewAdapter

@AndroidEntryPoint
class FinanceFragment : Fragment() {
    companion object {
        private const val TAG = "FinanceFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: FinanceViewModel by viewModels()
    private var _binding: FragmentFinanceBinding? = null
    private val binding get() = _binding!!

    //Data
    private var adapter: BasicRecyclerViewAdapter<Finance, FinanceViewHolder>? = null
    private var pageType: FinanceType? = null

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get Page Type, TODO: use argument
        pageType = FinanceType.fromInt(requireArguments().getInt("PAGE_TYPE"))
        //pageType = 3

        //Get Finance
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateFinances.collect {
                    if (it.status == Status.SUCCESS) {
                        initFinanceRecyclerView(it.financesList!!)
                    }
                }
            }
        }
        viewModel.fetchFinance(pageType!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Reform Page
        binding.type = pageType!!
        binding.pageTitle.text = resources.getStringArray(R.array.finance_title)[pageType!!.ordinal]
        if (pageType!! == FinanceType.Expenses ||
            pageType == FinanceType.PocketMoney ||
            pageType == FinanceType.Investments
        )
            binding.month.text =
                resources.getStringArray(R.array.months)[viewModel.getCurrentMonth().toInt() - 1]

        //Init ClickListener
        initClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    private fun initFinanceRecyclerView(finances: List<Finance>) {
        val bundle = Bundle()
        bundle.putInt("PageType", pageType!!.ordinal)
        adapter = BasicRecyclerViewAdapter.Builder(
            itemList = finances.toMutableList(),
            layoutId = R.layout.recyclerview_finance_item,
            vhClass = FinanceViewHolder::class.java,
            bundle = bundle,
            listener = null
        ).build()
        binding.financeList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.financeList.adapter = adapter
    }

    private fun initClickListeners() {
        //Back
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        //Go Stats
        binding.goStats.setOnClickListener {
            findNavController().navigate(R.id.finance_tracking)
        }

        //Back/Forth Month
        binding.backMonth.setOnClickListener {
            val months = resources.getStringArray(R.array.months)
            val index = months.indexOf(binding.month.text.toString())
            if (index == 0)
                binding.month.text = months[months.size - 1]
            else binding.month.text = months[index - 1]
        }
        binding.forwardMonth.setOnClickListener {
            val months = resources.getStringArray(R.array.months)
            val index = months.indexOf(binding.month.text.toString())
            if (index == months.size - 1)
                binding.month.text = months[0]
            else binding.month.text = months[index + 1]
        }

        //Add
        binding.addFinance.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("IsSavings", pageType == FinanceType.Savings)
            when (pageType) {
                FinanceType.Income -> {
                    findNavController().navigate(R.id.manage_income, bundle)
                }
                FinanceType.Savings -> {
                    findNavController().navigate(R.id.manage_income, bundle)
                }
                FinanceType.Debt -> {
                    val dialog = BaseDialog.Builder(
                        item = null,
                        layoutId = R.layout.dialog_manage_debt,
                        vhClass = ManageDebtViewHolder::class.java,
                        bundle = Bundle(),
                        listener = null
                    ).build()
                    dialog.show(childFragmentManager, "ManageDebtDialog")
                }
                else -> {
                    val bundle = Bundle()
                    bundle.putInt("PageType", pageType!!.ordinal)
                    val dialog = BaseDialog.Builder(
                        item = null,
                        layoutId = R.layout.dialog_manage_expenditure,
                        vhClass = ManageExpenditureViewHolder::class.java,
                        bundle = bundle,
                        listener = object: ManageExpenditureViewHolder.ExpenditureListener {
                            override fun onExpenditureSaved(expenditure: Expenditure) {
                                TODO("Not yet implemented")
                            }
                        }
                    ).build()
                    dialog.show(childFragmentManager, "ManageExpenditureDialog")
                }
            }
        }
    }

}