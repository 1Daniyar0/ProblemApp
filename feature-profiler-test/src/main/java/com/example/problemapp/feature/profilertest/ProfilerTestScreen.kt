package com.example.problemapp.feature.profilertest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val PRIME_LIMIT = 1_000_000

@Composable
fun ProfilerTestScreen(
    modifier: Modifier = Modifier,
    // Подменяется в UI-тестах, чтобы контролировать момент окончания расчёта
    backgroundDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Нажмите кнопку") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Расчёт на Dispatchers.Default: UI остаётся отзывчивым
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    status = "Вычисляем..."
                    val start = System.currentTimeMillis()
                    val count = withContext(backgroundDispatcher) {
                        countPrimesUpTo(PRIME_LIMIT)
                    }
                    val elapsed = System.currentTimeMillis() - start
                    status = "Default: найдено простых чисел: $count, за $elapsed мс"
                    isLoading = false
                }
            },
            enabled = !isLoading
        ) {
            Text("Тяжёлые вычисления (Default)")
        }

        // Расчёт прямо на главном потоке: UI зависает, «Вычисляем...» не успевает отрисоваться
        Button(
            onClick = {
                isLoading = true
                status = "Вычисляем..."
                val start = System.currentTimeMillis()
                val count = countPrimesUpTo(PRIME_LIMIT)
                val elapsed = System.currentTimeMillis() - start
                status = "Main: найдено простых чисел: $count, за $elapsed мс"
                isLoading = false
            },
            enabled = !isLoading
        ) {
            Text("Тяжёлые вычисления (Main)")
        }

        Text(text = status)
    }
}

fun countPrimesUpTo(limit: Int): Int {
    var count = 0
    for (n in 2..limit) {
        var isPrime = true
        var d = 2
        while (d * d <= n) { // то же самое, что d <= sqrt(n)
            if (n % d == 0) {
                isPrime = false
                break
            }
            d++
        }
        if (isPrime) count++
    }
    return count
}

@Preview(showBackground = true)
@Composable
private fun ProfilerTestScreenPreview() {
    ProfilerTestScreen()
}
