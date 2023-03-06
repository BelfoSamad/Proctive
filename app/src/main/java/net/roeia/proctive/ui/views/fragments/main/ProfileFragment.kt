package net.roeia.proctive.ui.views.fragments.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.roeia.proctive.databinding.FragmentProfileBinding
import net.roeia.proctive.ui.viewmodels.main.ProfileViewModel
import net.roeia.proctive.ui.views.activities.MainActivity
import net.roeia.proctive.ui.views.activities.StartupActivity

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    companion object {
        private const val TAG = "ProfileFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: ProfileViewModel by viewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Init ClickListener
        initClickListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    private fun initClickListener() {
        //Back
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        //Logout
        binding.logout.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(requireContext(), StartupActivity::class.java))
            requireActivity().finish()
        }
    }

}