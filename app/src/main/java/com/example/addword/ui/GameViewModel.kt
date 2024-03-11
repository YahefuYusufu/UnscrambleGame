package com.example.addword.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.addword.data.SCORE_INCREASE
import com.example.addword.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()

     var userGuess by mutableStateOf("")

    init {
        restGame()
    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }
    fun updateGameState(updatedScoer: Int) {
        _uiState.update {currentState ->
            currentState.copy(
             isGuessedWordWrong = false,
                currentScrambledWord = pickRandomWordAndShuffle(),
                score = updatedScoer,
                currentWordCount = currentState.currentWordCount.inc()

            )
        }
    }

    private fun pickRandomWordAndShuffle(): String {
        // Continue picking up a new random word until you get one that hasn't been used before
        currentWord = allWords.random()
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
           return shuffleCurrentWord(currentWord)
        }
    }

   private fun shuffleCurrentWord(word: String): String {
       val tempWord = word.toCharArray()
       // Scramble the word
       tempWord.shuffle()

       while (String(tempWord) == word) {
           tempWord.shuffle()
       }
       return String(tempWord)
   }

     fun checkUserGuess() {
        if (userGuess.equals(currentWord,ignoreCase = true)) {
            // User's guess is correct, increase the score
            // and call updateGameState() to prepare the game for next round
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        //reset your guess
        updateUserGuess("")
    }

    fun skipWord() {
        updateGameState(_uiState.value.score)
        // Reset user guess
        updateUserGuess("")
    }

    private fun restGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }
}