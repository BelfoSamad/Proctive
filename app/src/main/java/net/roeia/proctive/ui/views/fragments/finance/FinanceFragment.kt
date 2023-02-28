package net.roeia.proctive.ui.views.fragments.finance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.roeia.proctive.databinding.FragmentFinanceBinding
import net.roeia.proctive.ui.viewmodels.finance.FinanceViewModel

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

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinanceBinding.inflate(inflater, container, false)
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