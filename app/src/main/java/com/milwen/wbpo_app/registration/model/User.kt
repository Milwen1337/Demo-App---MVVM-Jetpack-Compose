package com.milwen.wbpo_app.registration.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "logged_in_user")
data class User(
    @SerializedName("id") @ColumnInfo(name = "id") @PrimaryKey val id: Int,
    @SerializedName("token") @ColumnInfo(name = "token") val token: String
)