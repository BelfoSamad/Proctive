package net.roeia.proctive.ui.views.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.roeia.proctive.R
import net.roeia.proctive.databinding.FragmentHomeBinding
import net.roeia.proctive.ui.viewmodels.main.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment() {
    companion object {
        private const val TAG = "HomeFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
    private fun initClickListeners() {
        //Drawer Menu
        binding.drawermenu.setOnClickListener {
            //TODO: Setup Drawer Menu
        }

        //Schedule
        binding.schedule.setOnClickListener {
            findNavController().navigate(R.id.go_schedule)
        }

        //Profile
        binding.profile.setOnClickListener {
            findNavController().navigate(R.id.go_profile)
        }

        //Go Journal
        binding.goJournal.setOnClickListener {
            findNavController().navigate(R.id.go_journal)
        }

        //Lock Journal
        binding.lockJournal.setOnClickListener {
            //TODO: Lock Journal
        }
    }

    fun goHabits(v: View) {
        findNavController().navigate(R.id.go_habit)
    }

}