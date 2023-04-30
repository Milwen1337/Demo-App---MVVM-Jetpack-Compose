package com.milwen.wbpo_app.registration.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.milwen.wbpo_app.R
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.registration.viewmodel.RegistrationViewModel
import com.milwen.wbpo_app.ui.main.BaseFragment

class RegistrationFragment: BaseFragment() {
    override val title: String
        get() = "Registrácia používateľa"
    override val debugTitle: String
        get() = "RegistrationFragment"

    private lateinit var viewModel: RegistrationViewModel
    override fun onCreate(si: Bundle?) {
        super.onCreate(si)
        App.log("RegistrationViewModel: fragmentInit")
        viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.registration_fragment, container, false)
    }
}