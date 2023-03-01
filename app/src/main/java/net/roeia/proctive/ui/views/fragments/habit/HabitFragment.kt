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
        val adapter = BasicRecyclerViewAdapter.Builder(
            itemList = habits.toMutableList(),
            layoutId = R.layout.recyclerview_habit_item,
            vhClass = ViewHolder::class.java,
            bundle = bundle,
            listener = object: ViewHolder.HabitClickListener {
                override fun onHabitChecked(habit: Habit, position: Int) {

                }
            }
        ).build()
        binding.habitsList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.habitsList.adapter = adapter
    }

    private fun initClickListeners() {
        //Back
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        //Update Habits
        binding.updateHabits.setOnClickListener {

        }

        binding.doneHabit.setOnClickListener {

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

    /***********************************************************************************************
     * ************************* RecyclerView Adapter
     */
    class ViewHolder constructor(private val b: RecyclerviewHabitItemBinding) :
        BasicRecyclerViewAdapter.BaseViewHolder<Habit>(b) {

        interface HabitClickListener : BasicRecyclerViewAdapter.BaseListener {

            fun onHabitChecked(habit: Habit, position: Int){}

            fun onHabitUnChecked(habit: Habit, position: Int){}

            fun onHabitUpdate(habit: Habit, position: Int){}

            fun onHabitDelete(habit: Habit, position: Int){}

        }

        override fun onBindViewHolder(
            model: Habit,
            bundle: Bundle,
            position: Int,
            listener: BasicRecyclerViewAdapter.BaseListener
        ) {
            b.habit = model
            b.position = position
            b.listener = listener as HabitClickListener
            b.isUpdate = bundle.getBoolean("isUpdate")

            //OnChecked Changed
            b.habitCheckbox.addOnCheckedStateChangedListener { checkBox, _ ->
                if (checkBox.isChecked)
                    listener.onHabitChecked(model, position)
                else listener.onHabitUnChecked(model, position)
            }
        }

    }

}