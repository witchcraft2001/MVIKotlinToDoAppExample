package org.mikhalchenkov.mytodoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import org.mikhalchenkov.mytodoapp.core.data.repository.TodoRepositoryImpl
import org.mikhalchenkov.mytodoapp.core.ui.theme.MyToDoAppTheme
import org.mikhalchenkov.mytodoapp.features.todo_list.store.TodoListStoreFactory
import org.mikhalchenkov.mytodoapp.features.todo_list.ui.TodoListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val storeFactory = DefaultStoreFactory()
        val repository = TodoRepositoryImpl()
        val todoListStoreFactory = TodoListStoreFactory(storeFactory, repository)
        val store = todoListStoreFactory.create()

        enableEdgeToEdge()
        setContent {
            MyToDoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        TodoListScreen(store)
                    }
                }
            }
        }
    }
}
