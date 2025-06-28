package com.todoapp.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.todoapp.data.database.TodoDatabase
import com.todoapp.data.entity.TodoEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class TodoDaoTest {

    private lateinit var todoDao: TodoDao
    private lateinit var db: TodoDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, TodoDatabase::class.java
        ).build()
        todoDao = db.todoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetTodo() = runBlocking {
        val todo = TodoEntity(title = "Test Todo", description = "Test Description")
        val id = todoDao.insertTodo(todo)
        
        val allTodos = todoDao.getAllTodos().first()
        assertEquals(1, allTodos.size)
        assertEquals("Test Todo", allTodos[0].title)
    }

    @Test
    fun updateTodoCompletionStatus() = runBlocking {
        val todo = TodoEntity(title = "Test Todo")
        val id = todoDao.insertTodo(todo).toInt()

        // Initially not completed
        val activeTodosBefore = todoDao.getActiveTodos().first()
        val completedTodosBefore = todoDao.getCompletedTodos().first()
        assertEquals(1, activeTodosBefore.size)
        assertEquals(0, completedTodosBefore.size)

        // Mark as completed
        todoDao.updateTodoCompletionStatus(id, true)

        val activeTodosAfter = todoDao.getActiveTodos().first()
        val completedTodosAfter = todoDao.getCompletedTodos().first()
        assertEquals(0, activeTodosAfter.size)
        assertEquals(1, completedTodosAfter.size)
        assertTrue(completedTodosAfter[0].isCompleted)
    }

    @Test
    fun deleteCompletedTodos() = runBlocking {
        // Insert completed and active todos
        val completedTodo = TodoEntity(title = "Completed Todo", isCompleted = true)
        val activeTodo = TodoEntity(title = "Active Todo", isCompleted = false)
        
        todoDao.insertTodo(completedTodo)
        todoDao.insertTodo(activeTodo)

        // Delete completed todos
        todoDao.deleteCompletedTodos()

        val allTodos = todoDao.getAllTodos().first()
        assertEquals(1, allTodos.size)
        assertEquals("Active Todo", allTodos[0].title)
    }
}