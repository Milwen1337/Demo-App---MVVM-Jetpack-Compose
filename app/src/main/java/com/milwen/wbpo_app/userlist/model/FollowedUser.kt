package com.milwen.wbpo_app.userlist.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "followed_users")
data class FollowedUser(
    @ColumnInfo(name = "id") @PrimaryKey val id: Int
)