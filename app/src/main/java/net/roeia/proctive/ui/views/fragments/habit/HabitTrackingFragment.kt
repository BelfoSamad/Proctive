package net.roeia.proctive.ui.views.fragments.habit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.roeia.proctive.databinding.FragmentHabitTrackingBinding
import net.roeia.proctive.ui.viewmodels.habit.HabitTrackingViewModel

@AndroidEntryPoint
class HabitTrackingFragment : Fragment() {
    companion object {
        private const val TAG = "ManageHabitFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: HabitTrackingViewModel by viewModels()
    private var _binding: FragmentHabitTrackingBinding? = null
    private val binding get() = _binding!!

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitTrackingBinding.inflate(inflater, container, false)
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