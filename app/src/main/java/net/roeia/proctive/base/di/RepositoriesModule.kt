package net.roeia.proctive.base.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.roeia.proctive.data.datasources.local.db.AppDatabase
import net.roeia.proctive.data.datasources.local.db.dao.TodoDao
import net.roeia.proctive.data.repositories.TodoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoriesModule {

    @Singleton
    @Provides
    fun provideTodoRepository(db: AppDatabase): TodoRepository =
        TodoRepository(db.getTodoDao(), db.getHabitsDao())

}