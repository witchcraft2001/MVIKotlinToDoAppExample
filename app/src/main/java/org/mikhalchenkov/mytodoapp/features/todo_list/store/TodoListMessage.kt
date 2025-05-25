package org.mikhalchenkov.mytodoapp.features.todo_list.store

import org.mikhalchenkov.mytodoapp.core.domain.entity.Todo

sealed class TodoListMessage {
    data object Loading: TodoListMessage()
    data class Loaded(val todos: List<Todo>) : TodoListMessage()
    data class Added(val todo: Todo) : TodoListMessage()
    data class Toggled(val id: Long) : TodoListMessage()
    data class Deleted(val id: Long) : TodoListMessage()
    data class Error(val message: String) : TodoListMessage()
}