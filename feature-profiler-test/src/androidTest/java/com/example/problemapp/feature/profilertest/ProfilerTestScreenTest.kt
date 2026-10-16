package com.example.problemapp.feature.profilertest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

private const val DEFAULT_BUTTON = "Тяжёлые вычисления (Default)"
private const val MAIN_BUTTON = "Тяжёлые вычисления (Main)"
private const val LOADING = "Вычисляем..."
private const val PRIMES_UP_TO_1M = 78_498
private const val RESULT_TIMEOUT_MS = 30_000L

@RunWith(AndroidJUnit4::class)
class ProfilerTestScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val gatedDispatcher = GatedDispatcher()

    @After
    fun tearDown() {
        gatedDispatcher.shutdown()
    }

    private fun setScreen() {
        composeRule.setContent {
            ProfilerTestScreen(backgroundDispatcher = gatedDispatcher)
        }
    }

    @Test
    fun initialState_bothButtonsEnabled_andHintShown() {
        setScreen()

        composeRule.onNodeWithText(DEFAULT_BUTTON).assertIsEnabled()
        composeRule.onNodeWithText(MAIN_BUTTON).assertIsEnabled()
        composeRule.onNodeWithText("Нажмите кнопку").assertIsDisplayed()
    }

    @Test
    fun defaultButton_whileComputing_showsLoadingAndDisablesButtons() {
        setScreen()

        composeRule.onNodeWithText(DEFAULT_BUTTON).performClick()

        // Расчёт ещё не запущен: диспетчер держит его до release()
        composeRule.onNodeWithText(LOADING).assertIsDisplayed()
        composeRule.onNodeWithText(DEFAULT_BUTTON).assertIsNotEnabled()
        composeRule.onNodeWithText(MAIN_BUTTON).assertIsNotEnabled()
        check(gatedDispatcher.hasPendingWork()) { "Расчёт должен быть отправлен в фоновый диспетчер" }
    }

    @Test
    fun defaultButton_afterComputing_showsResultAndEnablesButtons() {
        setScreen()

        composeRule.onNodeWithText(DEFAULT_BUTTON).performClick()
        gatedDispatcher.release()

        val result = "Default: найдено простых чисел: $PRIMES_UP_TO_1M"
        composeRule.waitUntil(RESULT_TIMEOUT_MS) {
            composeRule.onAllNodesWithText(result, substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(result, substring = true).assertIsDisplayed()
        composeRule.onNodeWithText(DEFAULT_BUTTON).assertIsEnabled()
        composeRule.onNodeWithText(MAIN_BUTTON).assertIsEnabled()
    }

    @Test
    fun mainButton_computesOnMainThread_andShowsResultImmediately() {
        setScreen()

        composeRule.onNodeWithText(MAIN_BUTTON).performClick()

        // Расчёт синхронный: после клика результат уже готов, фоновый диспетчер не задействован
        composeRule.onNodeWithText("Main: найдено простых чисел: $PRIMES_UP_TO_1M", substring = true)
            .assertIsDisplayed()
        composeRule.onNodeWithText(LOADING).assertDoesNotExist()
        composeRule.onNodeWithText(DEFAULT_BUTTON).assertIsEnabled()
        composeRule.onNodeWithText(MAIN_BUTTON).assertIsEnabled()
        check(!gatedDispatcher.hasPendingWork()) { "Main-кнопка не должна использовать фоновый диспетчер" }
    }
}

/** Фоновый диспетчер, который копит задачи и выполняет их только после [release]. */
private class GatedDispatcher : CoroutineDispatcher() {
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val pending = mutableListOf<Runnable>()
    private var released = false

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        synchronized(this) {
            if (!released) {
                pending += block
                return
            }
        }
        executor.execute(block)
    }

    fun release() {
        val toRun = synchronized(this) {
            released = true
            pending.toList().also { pending.clear() }
        }
        toRun.forEach(executor::execute)
    }

    fun hasPendingWork(): Boolean = synchronized(this) { pending.isNotEmpty() }

    fun shutdown() {
        executor.shutdownNow()
    }
}
