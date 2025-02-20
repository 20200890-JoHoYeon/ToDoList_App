package com.example.todoList.di

import android.content.Context
import com.example.todoList.model.ItemDatabase
import com.example.todoList.model.ItemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideItemDatabase(@ApplicationContext context: Context): ItemDatabase {
        return ItemDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideItemRepository(itemDatabase: ItemDatabase): ItemRepository {
        return ItemRepository(itemDatabase.itemDao())
    }
}