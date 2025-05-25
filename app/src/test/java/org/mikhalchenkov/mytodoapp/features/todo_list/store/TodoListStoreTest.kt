package org.mikhalchenkov.mytodoapp.features.todo_list.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mikhalchenkov.mytodoapp.core.domain.repository.TodoRepository
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.assertEquals
import org.mikhalchenkov.mytodoapp.core.domain.entity.Todo
import io.mockk.mockk
import io.mockk.coEvery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class TodoListStoreTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var repository: TodoRepository
    private lateinit var storeFactory: StoreFactory
    private lateinit var store: Store<TodoListIntent, TodoListState, Nothing>

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        repository = mockk<TodoRepository>()
        storeFactory = DefaultStoreFactory()
        store = TodoListStoreFactory(
            storeFactory = DefaultStoreFactory(),
            repository = repository
        ).create()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when AddTodo intent then todo is added to state`(): Unit = runTest(testDispatcher.scheduler) {
        // Given
        val newTodoText = "Test todo"
        val expectedTodo = Todo(1L, newTodoText, false)
        coEvery { repository.addTodo(newTodoText) } returns expectedTodo

        // When
        store.accept(TodoListIntent.AddTodo(newTodoText))
        advanceUntilIdle()

        // Then
        assertEquals(1, store.state.todos.size)
        assertEquals(expectedTodo, store.state.todos.first())
    }
}