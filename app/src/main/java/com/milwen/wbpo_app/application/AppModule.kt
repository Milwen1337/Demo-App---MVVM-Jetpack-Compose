package com.milwen.wbpo_app.application

import com.milwen.wbpo_app.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplication(): App {
        return App.instance
    }

    @Provides
    @Singleton
    fun provideAppDatabase(application: App): AppDatabase {
        return AppDatabase.getInstance(application)
    }
}




