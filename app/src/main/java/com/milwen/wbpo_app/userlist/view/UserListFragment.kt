package com.milwen.wbpo_app.userlist.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import com.milwen.wbpo_app.R
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.databinding.UserListFragmentBinding
import com.milwen.wbpo_app.onDoubleTouchProtectClick
import com.milwen.wbpo_app.setVisibleNotGone
import com.milwen.wbpo_app.ui.main.BaseFragment
import com.milwen.wbpo_app.userlist.model.LoadedUser
import com.milwen.wbpo_app.userlist.viewmodel.UserListViewModel
import com.bumptech.glide.Glide
import com.milwen.wbpo_app.userlist.viewmodel.UserPayload
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserListFragment: BaseFragment() {
    override val titleId: Int
        get() = R.string.user_list_fragment_title
    override val debugTitle: String
        get() = "UserListFragment"

    private val _adapter = Adapter()
    private val viewModel: UserListViewModel by viewModels()

    private lateinit var binding: UserListFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.user_list_fragment, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.apply {
            users.observe(viewLifecycleOwner) { users->
                updateUsers(users)
            }
            toastMessage.observe(viewLifecycleOwner) { message->
                showDataLoadingError(message)
            }
            areDataLoading.observe(viewLifecycleOwner) { areDataLoading->
                changeViewState(areDataLoading)
            }
            maybeLoadAgain.observe(viewLifecycleOwner){ maybeLoadAgain->
                changeTryAgainVisibility(maybeLoadAgain)
            }
        }

        binding.list.apply {
            adapter = _adapter
            layoutManager = LinearLayoutManager(requireContext())

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount - 1
                    val lastCompletelyVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()
                    App.log("UserListFragment: onScrolled: lastCompletelyVisiblePosition: $lastCompletelyVisiblePosition, totalItemCount: $totalItemCount")
                    if (lastCompletelyVisiblePosition == totalItemCount) {
                        viewModel.loadAdditionalUsers()
                    }
                }
            })

        }
        return binding.root
    }

    private fun changeViewState(isLoading: Boolean){
        App.log("UserListFragment: changeViewState: isLoading: $isLoading")
        binding.apply {
            list.setVisibleNotGone(!isLoading)
            mProgressBar.setVisibleNotGone(isLoading)
        }
    }

    private fun showDataLoadingError(err: String){
        App.showToast(requireContext(), err)
    }

    private fun changeTryAgainVisibility(isVisible: Boolean){
        App.log("UserListFragment: changeTryAgainVisibility: $isVisible")
        binding.loadAgainButton.setVisibleNotGone(isVisible)
    }

    private fun updateUsers(userPayload: UserPayload){
        App.log("UserListFragment: updateUsers: ${userPayload.users.size}")
        val adapterItems = mutableListOf<UserAdapterItem>()
        val followedUsers = userPayload.followedUsers
        val viewUsers = userPayload.users.map { usr-> UserItem(usr, followedUsers.find { it.id == usr.id } != null) }
        val itemCount = _adapter.itemCount
        adapterItems.apply {
            addAll(viewUsers)
            if (!userPayload.fullyLoaded) add(LoaderItem())
        }
        App.log("UserListFragment: updateUsers: final ${viewUsers.size}")
        _adapter.items = adapterItems
        _adapter.notifyItemRangeChanged(adapterItems.size - itemCount, itemCount)
    }

    enum class ViewItemType(val type: Int) {
        USER(1),
        LOADING_VIEW(2)
    }
    abstract class UserAdapterItem(val type: ViewItemType)
    class UserItem(val t: LoadedUser, var isFollowed: Boolean = false)
        : UserAdapterItem(ViewItemType.USER)
    class LoaderItem: UserAdapterItem(ViewItemType.LOADING_VIEW)

    private open inner class ItemViewHolder(root: ViewGroup): RecyclerView.ViewHolder(root)
    private inner class UserItemViewHolder(val root: ViewGroup): ItemViewHolder(root){
        val userAvatar = root.findViewById<ShapeableImageView>(R.id.userAvatar)
        val userName = root.findViewById<TextView>(R.id.userName)
        val userEmail = root.findViewById<TextView>(R.id.userEmail)
        val followButton = root.findViewById<ImageButton>(R.id.followButton)
        val unfollowButton = root.findViewById<ImageButton>(R.id.unfollowButton)

        @SuppressLint("SetTextI18n")
        fun bind(item: UserItem, onFollowClick: ()->Unit) {
            with(item) {
                loadAvatar(item.t.avatar)
                setButtonState(item.isFollowed)
                userName.text = "${item.t.first_name} ${item.t.last_name}"
                userEmail.text = item.t.email
                followButton.apply {
                    onDoubleTouchProtectClick {
                        onFollowClick.invoke()
                    }
                }
                unfollowButton.apply {
                    onDoubleTouchProtectClick {
                        onFollowClick.invoke()
                    }
                }
            }
        }

        private fun setButtonState(isFollowed: Boolean){
            followButton.setVisibleNotGone(!isFollowed)
            unfollowButton.setVisibleNotGone(isFollowed)
        }

        private fun loadAvatar(avatar: String?){
            val options: RequestOptions = RequestOptions.centerInsideTransform()
                .placeholder(R.drawable.user_avatar_placeholder)
                .error(R.drawable.user_avatar_placeholder)

            Glide.with(requireContext()).load(avatar).apply(options).into(userAvatar)
        }
    }

    private inner class LoadingItemViewHolder(val root: ViewGroup): ItemViewHolder(root)

    private inner class Adapter : RecyclerView.Adapter<ItemViewHolder>() {
        var items = emptyList<UserAdapterItem>()

        override fun getItemCount() = items.size

        override fun getItemViewType(position: Int): Int {
            return items[position].type.type
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            return when (viewType) {
                ViewItemType.USER.type -> {
                    UserItemViewHolder(
                        layoutInflater.inflate(
                            R.layout.user_list_item,
                            parent,
                            false
                        ) as ViewGroup
                    )
                }
                else -> {
                    UserItemViewHolder(
                        layoutInflater.inflate(
                            R.layout.user_loading_view,
                            parent,
                            false
                        ) as ViewGroup
                    )
                }
            }
        }

        override fun onBindViewHolder(vh: ItemViewHolder, position: Int) {
            val r = items[position]
            when (r.type) {
                ViewItemType.USER -> {
                    (vh as? UserItemViewHolder)?.bind(
                        r as UserItem,
                        onFollowClick = {
                            viewModel.changeFollowState(r.t)
                        }
                    )
                }
                ViewItemType.LOADING_VIEW -> {}
            }
        }
    }
}