package com.milwen.wbpo_app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.milwen.wbpo_app.registration.model.User
import com.milwen.wbpo_app.userlist.model.FollowedUser

@Database(entities = [User::class, FollowedUser::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun followedUsers(): FollowedUserDAO

    companion object{
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context):AppDatabase?{
            if (instance == null){
                synchronized(AppDatabase::class.java){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "main_database"
                    ).build()
                }
            }
            return instance
        }
    }
}