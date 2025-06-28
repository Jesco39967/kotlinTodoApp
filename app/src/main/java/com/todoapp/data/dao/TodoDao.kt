package com.todoapp.data.dao

import androidx.room.*
import com.todoapp.data.entity.TodoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos")
    fun getAllTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE is_completed = 0")
    fun getActiveTodos(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE is_completed = 1")
    fun getCompletedTodos(): Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity): Long

    @Update
    suspend fun updateTodo(todo: TodoEntity)

    @Query("UPDATE todos SET is_completed = :completed WHERE id = :todoId")
    suspend fun updateTodoCompletionStatus(todoId: Int, completed: Boolean)

    @Delete
    suspend fun deleteTodo(todo: TodoEntity)

    @Query("DELETE FROM todos WHERE is_completed = 1")
    suspend fun deleteCompletedTodos()
}