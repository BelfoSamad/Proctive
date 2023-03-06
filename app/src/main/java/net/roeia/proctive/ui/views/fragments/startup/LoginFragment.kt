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
import net.roeia.proctive.databinding.FragmentLoginBinding
import net.roeia.proctive.ui.viewmodels.startup.LoginViewModel
import net.roeia.proctive.ui.views.activities.MainActivity
import net.roeia.proctive.utils.*

@AndroidEntryPoint
class LoginFragment : Fragment() {
    companion object {
        private const val TAG = "LoginFragment"
    }

    /***********************************************************************************************
     * ************************* Declarations
     */
    private val viewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    /***********************************************************************************************
     * ************************* LifeCycle
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Init ClickListener
        initClickListeners()

        //Observe LiveData
        viewModel.loggedInLiveData.observe(viewLifecycleOwner) {
            if (it == null) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                when (it) {
                    AUTH_WRONG_CREDENTIALS_ERROR -> binding.emailInput.error = "Email or Password wrong"
                    AUTH_USER_COLLISION_ERROR -> binding.emailInput.error = "Another user with this email already exists"
                }
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
        binding.forgotPassword.setOnClickListener {
            //TODO: Go Forgot Password
        }

        binding.login.setOnClickListener {
            if (!formValid()) {
                viewModel.loginUser(
                    binding.emailInput.editText?.text.toString(),
                    binding.passwordInput.editText?.text.toString(),
                )
            }
        }

        binding.authGoogle.setOnClickListener {
            //TODO: Auth Google
        }

        binding.goRegister.setOnClickListener {
            findNavController().navigate(R.id.go_register)
        }
    }

    private fun formValid(): Boolean {
        var valid = true

        //Clear Errors
        binding.emailInput.error = ""
        binding.passwordInput.error = ""

        if (binding.emailInput.editText?.text.toString() == "") {
            binding.emailInput.error = "Username shouldn't be empty"
            valid = false
        }

        if (binding.passwordInput.editText?.text.toString() == "") {
            binding.passwordInput.error = "Password shouldn't be empty"
            valid = false
        }

        return valid
    }
}