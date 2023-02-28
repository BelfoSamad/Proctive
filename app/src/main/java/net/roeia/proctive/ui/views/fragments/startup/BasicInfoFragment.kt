package net.roeia.proctive.ui.views.fragments.startup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.roeia.proctive.databinding.FragmentBasicInfoBinding
import net.roeia.proctive.ui.viewmodels.startup.BasicInfoViewModel

@AndroidEntryPoint
class BasicInfoFragment : Fragment() {
    companion object {
        private const val TAG = "BasicInfoFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: BasicInfoViewModel by viewModels()
    private var _binding: FragmentBasicInfoBinding? = null
    private val binding get() = _binding!!

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasicInfoBinding.inflate(inflater, container, false)
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