package net.roeia.proctive.ui.views.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.roeia.proctive.databinding.FragmentJournalBinding
import net.roeia.proctive.ui.viewmodels.main.JournalViewModel

@AndroidEntryPoint
class JournalFragment : Fragment() {
    companion object {
        private const val TAG = "JournalFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: JournalViewModel by viewModels()
    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJournalBinding.inflate(inflater, container, false)
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