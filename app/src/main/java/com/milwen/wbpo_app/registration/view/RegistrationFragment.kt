package com.milwen.wbpo_app.registration.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.milwen.wbpo_app.R
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.database.AppDatabase
import com.milwen.wbpo_app.databinding.RegistrationFragmentBinding
import com.milwen.wbpo_app.registration.viewmodel.RegistrationViewModel
import com.milwen.wbpo_app.registration.viewmodel.RegistrationViewModelFactory
import com.milwen.wbpo_app.ui.main.BaseFragment
import com.milwen.wbpo_app.userlist.view.UserListFragment
import com.milwen.wbpo_app.userlist.viewmodel.UserListViewModel
import javax.inject.Inject

class RegistrationFragment: BaseFragment() {
    override val titleId: Int
        get() = R.string.registration_fragment_title
    override val debugTitle: String
        get() = "RegistrationFragment"


    private val viewModel: RegistrationViewModel by viewModels()

    private lateinit var binding: RegistrationFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.registration_fragment, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.apply {
            isRegButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
                changeViewStates(isEnabled)
            }
            isDataValid.observe(viewLifecycleOwner) { isEnabled ->
                changeButtonState(isEnabled)
            }
            toastMessage.observe(viewLifecycleOwner) { message->
                showRegistrationError(message)
            }
            finishRegistration.observe(viewLifecycleOwner) { finish->
                if (finish) finishRegistration()
            }
        }
        return binding.root
    }

    private fun changeViewStates(enabled: Boolean){
        binding.apply {
            userEmail.isEnabled = enabled
            userPassword.isEnabled = enabled
            registerButton.isEnabled = enabled
        }
    }

    private fun changeButtonState(enabled: Boolean){
        binding.registerButton.isEnabled = enabled
    }

    private fun showRegistrationError(err: String){
        App.showToast(requireContext(), err)
    }

    private fun finishRegistration(){
        mainActivity?.startFragment(UserListFragment())
    }
}