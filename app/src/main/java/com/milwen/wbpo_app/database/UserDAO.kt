package com.milwen.wbpo_app.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.milwen.wbpo_app.registration.model.User

@Dao
interface UserDAO {
    @Query("SELECT * FROM logged_in_user LIMIT 1")
    fun getUser(): User?

    @Query("SELECT * FROM logged_in_user WHERE token LIKE :token LIMIT 1")
    fun findByToken(token: String): User

    @Insert
    fun insertUser(user: User)

    @Delete
    fun delete(user: User)
}