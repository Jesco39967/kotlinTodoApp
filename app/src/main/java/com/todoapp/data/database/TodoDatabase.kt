package com.todoapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.todoapp.data.dao.TodoDao
import com.todoapp.data.entity.TodoEntity

@Database(
    entities = [TodoEntity::class],
    version = 2,  // Increment version for schema changes
    exportSchema = false
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        fun getDatabase(context: Context): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_database"
                )
                .fallbackToDestructiveMigration() // Simplest migration strategy
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}