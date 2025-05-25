package org.mikhalchenkov.mytodoapp.features.todo_list.store

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import kotlinx.coroutines.launch
import org.mikhalchenkov.mytodoapp.core.domain.repository.TodoRepository

class TodoListStoreFactory(
    private val storeFactory: StoreFactory,
    private val repository: TodoRepository,
) {
    fun create(): TodoListStore = object : TodoListStore,
        Store<TodoListIntent, TodoListState, Nothing> by storeFactory.create(
            name = "TodoListStore",
            initialState = TodoListState(),
            bootstrapper = SimpleBootstrapper(TodoListAction.LoadTodos),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private inner class ExecutorImpl :
        CoroutineExecutor<TodoListIntent, TodoListAction, TodoListState, TodoListMessage, Nothing>() {
        override fun executeAction(action: TodoListAction) {
            when (action) {
                is TodoListAction.LoadTodos -> loadTodos()
            }
        }

        override fun executeIntent(intent: TodoListIntent) {
            when (intent) {
                is TodoListIntent.LoadTodos -> loadTodos()
                is TodoListIntent.ToggleTodo -> toggleTodo(intent.id)
                is TodoListIntent.AddTodo -> addTodo(intent.text)
                is TodoListIntent.DeleteTodo -> deleteTodo(intent.id)
            }
        }

        private fun loadTodos() {
            scope.launch {
                dispatch(TodoListMessage.Loading)
                try {
                    val todos = repository.getTodos()
                    dispatch(TodoListMessage.Loaded(todos))
                } catch (e: Exception) {
                    dispatch(TodoListMessage.Error(e.message ?: "Unknown error"))
                }
            }
        }

        private fun toggleTodo(id: Long) {
            scope.launch {
                dispatch(TodoListMessage.Loading)
                try {
                    repository.toggleTodo(id)
                    dispatch(TodoListMessage.Toggled(id))
                } catch (e: Exception) {
                    dispatch(TodoListMessage.Error(e.message ?: "Failed to toggle todo"))
                }
            }
        }

        private fun addTodo(text: String) {
            scope.launch {
                dispatch(TodoListMessage.Loading)
                try {
                    val todo = repository.addTodo(text)
                    dispatch(TodoListMessage.Added(todo))
                } catch (e: Exception) {
                    dispatch(TodoListMessage.Error(e.message ?: "Failed to add todo"))
                }
            }
        }

        private fun deleteTodo(id: Long) {
            scope.launch {
                dispatch(TodoListMessage.Loading)
                try {
                    repository.deleteTodo(id)
                    dispatch(TodoListMessage.Deleted(id))
                } catch (e: Exception) {
                    dispatch(TodoListMessage.Error(e.message ?: "Failed to delete todo"))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<TodoListState, TodoListMessage> {
        override fun TodoListState.reduce(msg: TodoListMessage): TodoListState =
            when (msg) {
                is TodoListMessage.Loading -> copy(isLoading = true, error = null)
                is TodoListMessage.Loaded -> copy(todos = msg.todos, isLoading = false)
                is TodoListMessage.Toggled -> copy(
                    todos = todos.map { todo ->
                        if (todo.id == msg.id) {
                            todo.copy(isCompleted = !todo.isCompleted)
                        } else {
                            todo
                        }
                    },
                    isLoading = false
                )

                is TodoListMessage.Added -> copy(todos = todos + msg.todo, isLoading = false)
                is TodoListMessage.Deleted -> copy(
                    todos = todos.filter { it.id != msg.id },
                    isLoading = false
                )

                is TodoListMessage.Error -> copy(error = msg.message, isLoading = false)
            }
    }
}