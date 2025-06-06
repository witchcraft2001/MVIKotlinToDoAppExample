package org.mikhalchenkov.mytodoapp.features.todo_list.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import org.mikhalchenkov.mytodoapp.core.domain.entity.Todo
import org.mikhalchenkov.mytodoapp.core.extensions.collectAsState
import org.mikhalchenkov.mytodoapp.features.todo_list.store.TodoListIntent
import org.mikhalchenkov.mytodoapp.features.todo_list.store.TodoListStore

@Composable
fun TodoListScreen(store: TodoListStore) {
    val state by store.collectAsState()
    var newTodoText by remember { mutableStateOf("") }

    val rememberedOnToggleTodo = remember(store) {
        { id: Long ->
            store.accept(TodoListIntent.ToggleTodo(id))
        }
    }

    val rememberedOnDeleteTodo = remember(store) {
        { id: Long ->
            store.accept(TodoListIntent.DeleteTodo(id))
        }
    }

    val rememberedOnAddTodo = remember(store) {
        {
            if (newTodoText.isNotBlank()) {
                store.accept(TodoListIntent.AddTodo(newTodoText))
                newTodoText = ""
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newTodoText,
                onValueChange = remember { { newTodoText = it } },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Add Task") }
            )

            IconButton(onClick = rememberedOnAddTodo) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            state.isLoading -> LoadingContent()
            state.error != null -> MessageContent(
                state.error ?: "Something was wrong",
                isError = true
            )

            state.todos.isEmpty() -> MessageContent("Nothing to show yet, add your first task!")
            else -> TodoListContent(
                state.todos,
                onToggleTodo = rememberedOnToggleTodo,
                onDeleteTodo = rememberedOnDeleteTodo,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun TodoListContent(
    todos: ImmutableList<Todo>,
    onToggleTodo: (Long) -> Unit,
    onDeleteTodo: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(todos.size, key = { index -> todos[index].id }) { index ->
            val todo = todos[index]
            TodoItem(
                todo = todo,
                onToggle = remember(todo.id, onToggleTodo) { { onToggleTodo(todo.id) } },
                onDelete = remember(todo.id, onDeleteTodo) { { onDeleteTodo(todo.id) } }
            )
        }
    }
}

@Composable
private fun MessageContent(
    error: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isError) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                }
            )
        ) {
            Text(
                text = error,
                modifier = Modifier.padding(16.dp),
                color = if (isError) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                }
            )
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
