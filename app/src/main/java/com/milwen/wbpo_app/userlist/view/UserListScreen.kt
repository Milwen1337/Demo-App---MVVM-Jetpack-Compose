package com.milwen.wbpo_app.userlist.view

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.milwen.wbpo_app.*
import com.milwen.wbpo_app.R
import com.milwen.wbpo_app.application.App
import com.milwen.wbpo_app.userlist.model.LoadedUser
import com.milwen.wbpo_app.userlist.viewmodel.UserListViewModelCompose
import com.milwen.wbpo_app.userlist.viewmodel.UserPayload

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    viewModel: UserListViewModelCompose = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current

    val usersState = rememberUpdatedState(newValue = viewModel.users.value)
    val isLoading = rememberUpdatedState(newValue = viewModel.areDataLoading.value)
    val maybeLoadAgain = rememberUpdatedState(newValue = viewModel.maybeLoadAgain.value)
    val errorMessage = rememberUpdatedState(newValue = viewModel.toastMessage.value)
    val formattedUsers = updateUsers(usersState.value)

    if (errorMessage.value.isNotEmpty()){
        showDataLoadingError(errorMessage.value)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = context.getString(R.string.user_list_fragment_title)) }) }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
        ) {
            if (maybeLoadAgain.value) {
                Button(
                    onClick = { viewModel.loadAgain() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Text(text = context.getString(R.string.button_load_again))
                }
            }

            if (isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .wrapContentSize(Alignment.Center)
                )
            } else {
                UserList(
                    fullyLoaded = usersState.value.fullyLoaded,
                    users = formattedUsers,
                    onItemChanged = { user-> viewModel.changeFollowState(user.t)},
                    onEndReached = { viewModel.loadAdditionalUsers() })
            }
        }
    }
}

private fun updateUsers(userPayload: UserPayload): List<UserItem>{
    App.log("UserListFragment: updateUsers: ${userPayload.users.size}")
    val followedUsers = userPayload.followedUsers
    val viewUsers = userPayload.users.map { usr->
        UserItem(usr, followedUsers.find { it.id == usr.id } != null)
    }
    return viewUsers
}

@Composable
fun UserList(
    fullyLoaded: Boolean,
    users: List<UserItem>,
    onItemChanged: (UserItem) -> Unit, onEndReached: () -> Unit
) {
    LazyColumn {
        items(users.size) { userItem ->
            UserItemRow(userItem = users[userItem]){ onItemChanged.invoke(users[userItem]) }
        }
        item {
            if (!fullyLoaded) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .wrapContentSize(Alignment.Center)
                )
                // Load more items when reaching the end of the list
                onEndReached()
            }
        }
    }
}

class UserItem(val t: LoadedUser, var isFollowed: Boolean = false)
@Composable
fun UserItemRow(userItem: UserItem, onStateUpdate: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            userAvatar(userItem.t.avatar)
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                userData(userItem)
            }
            followButton(userItem){ onStateUpdate.invoke() }
        }
    }
}

@Composable
fun userAvatar(url: String){
    Box(
        modifier = Modifier
            .size(50.dp)
            .padding(4.dp)
    ) {
        AsyncImage(
            placeholder = painterResource(R.drawable.user_avatar_placeholder),
            model = url,
            contentDescription = "User Avatar",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )
    }
}

@Composable
fun userData(userItem: UserItem){
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text(
            text = userItem.t.first_name + " " + userItem.t.last_name,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = userItem.t.email,
            fontSize = 16.sp
        )
    }
}

@Composable
fun followButton(userItem: UserItem, onStateUpdate: ()->Unit){
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val iconColor = if (userItem.isFollowed){ colorAccept
    } else { colorDecline }
    val containerColor = iconColor.copy(alpha = if (isPressed) 0.4f else 0f)

    IconButton(
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
            contentColor = iconColor,
            disabledContainerColor = colorGrey,
            disabledContentColor = colorWhite
        ),
        interactionSource = interactionSource,
        onClick = { onStateUpdate() }
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = if (userItem.isFollowed) R.drawable.user_follow else R.drawable.user_unfollow),
            contentDescription = "Follow Button Icon",
            tint = iconColor.copy(alpha = 1f)
        )
    }
}

@Composable
@Preview
fun FollowButtonPreview() {
    val userItem = UserItem(
        LoadedUser(
            0,
            "mockuser@test.com",
            "John", "Doe",
            "https://png.pngtree.com/png-vector/20190223/ourmid/pngtree-vector-picture-icon-png-image_695350.jpg"),
        false
    )

    followButton(userItem = userItem) {}
}


@Composable
fun showDataLoadingError(message: String) {
    val context = LocalContext.current
    App.showToast(context, message)
}

