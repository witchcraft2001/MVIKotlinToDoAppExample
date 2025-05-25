package org.mikhalchenkov.mytodoapp.features.todo_list.store

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.mikhalchenkov.mytodoapp.core.domain.entity.Todo

data class TodoListState(
    val todos: ImmutableList<Todo> = persistentListOf(),
    val isLoading: Boolean = false,
    val error: String? = null
)
