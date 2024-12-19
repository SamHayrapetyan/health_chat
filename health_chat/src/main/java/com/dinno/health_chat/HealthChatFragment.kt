package com.dinno.health_chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.dinno.health_chat.api.HealthChatManagerHolder
import com.dinno.health_chat.model.HealthChatState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class HealthChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state by HealthChatManagerHolder.manager!!.getChatState().collectAsStateWithLifecycle(
                    initialValue = HealthChatState.Loading,
                    lifecycle = LocalLifecycleOwner.current.lifecycle
                )
                HealthChatScreen(state = state, onNewIntent = { HealthChatManagerHolder.manager!!.handleIntent(it) })
            }
        }
    }

    companion object {
        fun newInstance() = HealthChatFragment()
    }
}

@Composable
private fun <T> Flow<T>.collectAsStateWithLifecycle(
    initialValue: T,
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext
): State<T> {
    return produceState(initialValue, this, lifecycle, minActiveState, context) {
        lifecycle.repeatOnLifecycle(minActiveState) {
            if (context == EmptyCoroutineContext) {
                this@collectAsStateWithLifecycle.collect { this@produceState.value = it }
            } else withContext(context) {
                this@collectAsStateWithLifecycle.collect { this@produceState.value = it }
            }
        }
    }
}
