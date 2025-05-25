package org.mikhalchenkov.mytodoapp.features.todo_list.store

import com.arkivanov.mvikotlin.core.store.Store

interface TodoListStore: Store<TodoListIntent, TodoListState, Nothing>
