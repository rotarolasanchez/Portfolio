package utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

/**
 * Utilitario para controlar corrutinas en tests unitarios KMP.
 *
 * Uso recomendado:
 *   val testDispatcher = StandardTestDispatcher()
 *   Dispatchers.setMain(testDispatcher)
 *   // ... test ...
 *   Dispatchers.resetMain()
 *
 * En KMP no existe JUnit Rule, por eso se expone como funciones
 * de setup/teardown que cada test llama manualmente.
 */
@OptIn(ExperimentalCoroutinesApi::class)
object TestCoroutineRule {

    val scheduler = TestCoroutineScheduler()
    val dispatcher = StandardTestDispatcher(scheduler)

    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    fun tearDown() {
        Dispatchers.resetMain()
    }
}

