package com.example.addword.ui


data class GameUiState (
    val currentScrambledWord: String = "",
    val isGuessedWordWrong: Boolean = false
)