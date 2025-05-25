package org.mikhalchenkov.mytodoapp.features.todo_list.store

import org.mikhalchenkov.mytodoapp.core.domain.entity.Todo

data class TodoListState(
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
