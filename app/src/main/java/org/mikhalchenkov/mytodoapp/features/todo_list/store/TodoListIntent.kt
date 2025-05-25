package org.mikhalchenkov.mytodoapp.features.todo_list.store

sealed class TodoListIntent {
    data object LoadTodos : TodoListIntent()
    data class AddTodo(val text: String) : TodoListIntent()
    data class ToggleTodo(val id: Long) : TodoListIntent()
    data class DeleteTodo(val id: Long) : TodoListIntent()
}