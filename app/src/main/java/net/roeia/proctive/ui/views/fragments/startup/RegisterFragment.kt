package net.roeia.proctive.ui.views.fragments.startup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.roeia.proctive.R
import net.roeia.proctive.databinding.FragmentRegisterBinding
import net.roeia.proctive.ui.viewmodels.startup.RegisterViewModel
import net.roeia.proctive.ui.views.activities.MainActivity

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    companion object {
        private const val TAG = "RegisterFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: RegisterViewModel by viewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Init ClickListeners
        initClickListeners()

        //LiveData Observer
        viewModel.registeredLiveData.observe(viewLifecycleOwner) {
            if (it == null) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                binding.passwordInput.error = "Weak Password"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /***********************************************************************************************
     * ************************* Methods
     */
    private fun initClickListeners() {
        binding.tos.setOnClickListener {
            //TODO: Terms of Service
        }

        binding.register.setOnClickListener {
            if (!formValid()) {
                viewModel.registerUser(
                    binding.usernameInput.editText?.text.toString(),
                    binding.emailInput.editText?.text.toString(),
                    binding.passwordInput.editText?.text.toString(),
                )
            }
        }

        binding.authGoogle.setOnClickListener {
            //TODO: Auth Google
        }

        binding.goLogin.setOnClickListener {
            findNavController().navigate(R.id.go_login)
        }
    }

    private fun formValid(): Boolean {
        var valid = true

        //Clear Errors
        binding.usernameInput.error = ""
        binding.emailInput.error = ""
        binding.passwordInput.error = ""
        binding.confirmPasswordInput.error = ""

        if (binding.usernameInput.editText?.text.toString() == "") {
            binding.usernameInput.error = "Username shouldn't be empty"
            valid = false
        }

        if (binding.emailInput.editText?.text.toString() == "") {
            binding.emailInput.error = "Email shouldn't be empty"
            valid = false
        }

        if (binding.passwordInput.editText?.text.toString() == "") {
            binding.passwordInput.error = "Password shouldn't be empty"
            valid = false
        }

        if (binding.confirmPasswordInput.editText?.text.toString() == "") {
            binding.confirmPasswordInput.error = "Confirm Password shouldn't be empty"
            valid = false
        }

        if (binding.passwordInput.editText?.text.toString() != binding.confirmPasswordInput.editText?.text.toString()) {
            binding.passwordInput.error = "Password and Confirm Password should be matching"
            binding.confirmPasswordInput.error = "Password and Confirm Password should be matching"
            valid = false
        }

        return valid
    }

}