package net.roeia.proctive.ui.views.fragments.habit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.roeia.proctive.databinding.FragmentHabitBinding
import net.roeia.proctive.ui.viewmodels.habit.HabitViewModel

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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /***********************************************************************************************
     * ************************* Methods
     */

}