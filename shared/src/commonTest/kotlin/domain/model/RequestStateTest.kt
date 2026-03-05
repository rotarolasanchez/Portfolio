package domain.model

import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertEquals

class RequestStateTest {

    @Test
    fun `Idle state`() {
        val state: RequestState<String> = RequestState.Idle
        assertIs<RequestState.Idle>(state)
    }

    @Test
    fun `Loading state`() {
        val state: RequestState<String> = RequestState.Loading
        assertIs<RequestState.Loading>(state)
    }

    @Test
    fun `Success state with data`() {
        val data = listOf("item1", "item2")
        val state: RequestState<List<String>> = RequestState.Success(data)

        assertIs<RequestState.Success<List<String>>>(state)
        assertEquals(data, state.data)
        assertEquals(2, state.data.size)
    }

    @Test
    fun `Error state with exception`() {
        val exception = Exception("Something went wrong")
        val state: RequestState<String> = RequestState.Error(exception)

        assertIs<RequestState.Error>(state)
        assertEquals("Something went wrong", state.error.message)
    }

    @Test
    fun `RequestState transitions`() {
        // Simular el flujo: Idle -> Loading -> Success
        var state: RequestState<String> = RequestState.Idle
        assertIs<RequestState.Idle>(state)

        state = RequestState.Loading
        assertIs<RequestState.Loading>(state)

        state = RequestState.Success("Resultado exitoso")
        assertIs<RequestState.Success<String>>(state)
        assertEquals("Resultado exitoso", state.data)
    }

    @Test
    fun `RequestState error transition`() {
        // Simular el flujo: Idle -> Loading -> Error
        var state: RequestState<String> = RequestState.Idle

        state = RequestState.Loading
        assertIs<RequestState.Loading>(state)

        state = RequestState.Error(RuntimeException("Network error"))
        assertIs<RequestState.Error>(state)
        assertEquals("Network error", state.error.message)
    }
}

