package net.roeia.proctive.ui.views.fragments.habit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import net.roeia.proctive.databinding.FragmentHabitBinding
import net.roeia.proctive.databinding.RecyclerviewHabitItemBinding
import net.roeia.proctive.databinding.RecyclerviewTodoItemBinding
import net.roeia.proctive.models.entities.Habit
import net.roeia.proctive.models.entities.Todo
import net.roeia.proctive.ui.viewmodels.habit.HabitViewModel
import net.roeia.proctive.ui.views.dialogs.ManageHabitDialog
import net.roeia.proctive.ui.views.fragments.todo.TodoFragment
import net.roeia.proctive.ui.views.viewholders.HabitViewHolder
import net.roeia.proctive.utils.adapters.BasicRecyclerViewAdapter

@AndroidEntryPoint
class HabitFragment : Fragment() {
    companion object {
        private const val TAG = "HabitFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: HabitViewModel by viewModels()
    private var _binding: FragmentHabitBinding? = null
    private val binding get() = _binding!!

    //Data
    private var habitsAdapter: BasicRecyclerViewAdapter<Habit, HabitViewHolder>? = null

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateHabits.collect {
                    if (it.status == Status.SUCCESS) {
                        initHabitsList(it.habitsList!!)
                    }
                }
            }
        }
        viewModel.fetchHabits()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
    private fun initHabitsList(habits: List<Habit>) {
        val bundle = Bundle()
        bundle.putBoolean("isUpdate", false)
        habitsAdapter = BasicRecyclerViewAdapter.Builder(
            itemList = habits.toMutableList(),
            layoutId = R.layout.recyclerview_habit_item,
            vhClass = HabitViewHolder::class.java,
            bundle = bundle,
            listener = object: HabitViewHolder.HabitClickListener {
                override fun onHabitChecked(habit: Habit, position: Int, checked: Boolean) {
                    viewModel.setChecked(habit.habitId!!, checked)
                }

                override fun onHabitUpdate(habit: Habit, position: Int) {
                    val dialog = ManageHabitDialog(habit, object: ManageHabitDialog.HabitListener {
                        override fun onHabitAdded(habit: Habit) {
                            viewModel.saveHabit(habit)
                        }
                    })
                    dialog.show(childFragmentManager, "ManageHabitDialog")
                }

                override fun onHabitDelete(habit: Habit, position: Int) {
                    viewModel.deleteHabit(habit)
                }
            }
        ).build()
        binding.habitsList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.habitsList.adapter = habitsAdapter
    }

    private fun initClickListeners() {
        //Back
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        //Update Habits
        binding.updateHabits.setOnClickListener {
            binding.addHabit.visibility = GONE
            binding.updateHabits.visibility = GONE
            binding.doneHabit.visibility = VISIBLE

            val bundle = Bundle()
            bundle.putBoolean("isUpdate", true)
            habitsAdapter?.setBundle(bundle)
        }

        binding.doneHabit.setOnClickListener {
            binding.addHabit.visibility = VISIBLE
            binding.updateHabits.visibility = VISIBLE
            binding.doneHabit.visibility = GONE

            val bundle = Bundle()
            bundle.putBoolean("isUpdate", false)
            habitsAdapter?.setBundle(bundle)
        }

        //Add Habit
        binding.addHabit.setOnClickListener {
            val dialog = ManageHabitDialog(null, object: ManageHabitDialog.HabitListener {
                override fun onHabitAdded(habit: Habit) {
                    viewModel.saveHabit(habit)
                }
            })
            dialog.show(childFragmentManager, "ManageHabitDialog")
        }
    }

}