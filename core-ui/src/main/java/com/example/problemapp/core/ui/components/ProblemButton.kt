package com.example.problemapp.core.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.problemapp.core.ui.theme.ProblemAppTheme

@Composable
fun ProblemButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(onClick = onClick, modifier = modifier, enabled = enabled) {
        Text(text = text)
    }
}

@Preview
@Composable
private fun ProblemButtonPreview() {
    ProblemAppTheme {
        ProblemButton(text = "Записаться", onClick = {})
    }
}