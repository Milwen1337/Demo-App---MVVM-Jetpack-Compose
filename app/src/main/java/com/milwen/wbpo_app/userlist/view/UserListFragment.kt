package com.milwen.wbpo_app.userlist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.milwen.wbpo_app.R
import com.milwen.wbpo_app.ui.main.BaseFragment
import com.milwen.wbpo_app.userlist.viewmodel.UserListViewModel

class UserListFragment: BaseFragment() {
    override val title: String
        get() = "Zoznam používateľov"
    override val debugTitle: String
        get() = "UserListFragment"

    private lateinit var viewModel: UserListViewModel
    override fun onCreate(si: Bundle?) {
        super.onCreate(si)
        viewModel = ViewModelProvider(this)[UserListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_list_fragment, container, false)
    }
}