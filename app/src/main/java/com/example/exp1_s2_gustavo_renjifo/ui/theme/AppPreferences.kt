package com.example.exp1_s2_gustavo_renjifo.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppPreferences {
    var isDarkTheme by mutableStateOf(false)
    var fontSizeScale by mutableStateOf(1.0f)

    fun toggleDarkTheme() {
        isDarkTheme = !isDarkTheme
    }

    fun increaseFontSize() {
        if (fontSizeScale < 1.5f) {
            fontSizeScale += 0.1f
        }
    }

    fun decreaseFontSize() {
        if (fontSizeScale > 0.8f) {
            fontSizeScale -= 0.1f
        }
    }

    fun resetFontSize() {
        fontSizeScale = 1.0f
    }
}