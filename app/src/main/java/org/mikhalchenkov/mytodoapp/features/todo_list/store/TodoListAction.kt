package org.mikhalchenkov.mytodoapp.features.todo_list.store

sealed class TodoListAction {
    data object LoadTodos : TodoListAction()
}