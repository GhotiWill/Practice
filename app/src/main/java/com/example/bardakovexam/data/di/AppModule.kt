package com.example.bardakovexam.data.di

import com.example.bardakovexam.data.remotes.ProductRepository
import com.example.bardakovexam.data.remotes.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideUserRepository() = UserRepository()

    @Provides
    @Singleton
    fun provideProductRepository() = ProductRepository()
}
