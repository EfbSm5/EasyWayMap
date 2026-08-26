package com.efbsm5.easyway.base

import com.efbsm5.easyway.state.IUiEffect
import com.efbsm5.easyway.state.IUiEvent
import com.efbsm5.easyway.state.IUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BaseViewModelTest {

    @Test
    fun dispatch_handlesEventsImmediatelyAndInOrder() {
        val viewModel = TestViewModel()

        viewModel.dispatch(TestEvent.Append(1))
        viewModel.dispatch(TestEvent.Append(2))

        assertEquals(listOf(1, 2), viewModel.uiState.value.values)
    }

    @Test
    fun stateUpdates_areAtomicAcrossConcurrentDispatchers() = runBlocking {
        val viewModel = TestViewModel()

        List(1_000) {
            async(Dispatchers.Default) {
                viewModel.dispatch(TestEvent.Increment)
            }
        }.awaitAll()

        assertEquals(1_000, viewModel.uiState.value.count)
    }

    @Test
    fun effects_emittedBeforeCollectionRetainOrder() = runBlocking {
        val viewModel = TestViewModel()

        viewModel.emitEffect(1)
        viewModel.emitEffect(2)

        assertEquals(
            listOf(TestEffect.Value(1), TestEffect.Value(2)),
            viewModel.effect.take(2).toList(),
        )
    }

    @Test
    fun effectAfterClear_isDropped() = runBlocking {
        val viewModel = TestViewModel()

        viewModel.clearForTest()
        viewModel.emitEffect(1)

        assertTrue(viewModel.effect.toList().isEmpty())
    }

    private class TestViewModel : BaseViewModel<TestEvent, TestState, TestEffect>() {
        override fun createInitialState(): TestState = TestState()

        override fun handleEvents(event: TestEvent) {
            when (event) {
                is TestEvent.Append -> setState { copy(values = values + event.value) }
                TestEvent.Increment -> setState { copy(count = count + 1) }
            }
        }

        fun dispatch(event: TestEvent) {
            setEvent(event)
        }

        fun emitEffect(value: Int) {
            setEffect { TestEffect.Value(value) }
        }

        fun clearForTest() {
            onCleared()
        }
    }

    private sealed interface TestEvent : IUiEvent {
        data class Append(val value: Int) : TestEvent
        data object Increment : TestEvent
    }

    private data class TestState(
        val values: List<Int> = emptyList(),
        val count: Int = 0,
    ) : IUiState

    private sealed interface TestEffect : IUiEffect {
        data class Value(val value: Int) : TestEffect
    }
}
