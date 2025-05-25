package org.mikhalchenkov.mytodoapp.core.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun <T : Any> Store<*, T, *>.collectAsState(): State<T> {
    val stateFlow = remember(this) { MutableStateFlow(state) }

    DisposableEffect(this) {
        val disposable = states(observer { stateFlow.value = it })
        onDispose { disposable.dispose() }
    }

    return stateFlow.collectAsState()
}
