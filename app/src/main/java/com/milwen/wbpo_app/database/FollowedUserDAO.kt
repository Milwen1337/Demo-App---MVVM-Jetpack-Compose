package com.milwen.wbpo_app.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.milwen.wbpo_app.userlist.model.FollowedUser

@Dao
interface FollowedUserDAO {
    @Query("SELECT * FROM followed_users")
    fun getFollowedUsers(): List<FollowedUser>?

    @Query("SELECT * FROM followed_users WHERE id LIKE :userId LIMIT 1")
    fun getFollowedUserOrNull(userId: Int): FollowedUser?

    @Insert
    fun addFollowedUser(user: FollowedUser)

    @Delete
    fun deleteFollowedUser(user: FollowedUser)
}