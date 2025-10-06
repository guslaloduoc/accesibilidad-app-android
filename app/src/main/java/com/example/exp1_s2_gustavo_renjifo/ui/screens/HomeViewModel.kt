package com.example.exp1_s2_gustavo_renjifo.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exp1_s2_gustavo_renjifo.data.room.entity.Message
import com.example.exp1_s2_gustavo_renjifo.services.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val saveStatus: SaveStatus = SaveStatus.Idle,
    val errorMessage: String? = null
)

sealed class SaveStatus {
    object Idle : SaveStatus()
    object Saving : SaveStatus()
    object Success : SaveStatus()
    data class Error(val message: String) : SaveStatus()
}

class HomeViewModel(
    private val userId: Int,
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            messageRepository.messages(userId).collect { messages ->
                _uiState.value = _uiState.value.copy(messages = messages)
            }
        }
    }

    fun onTextChange(text: String) {
        _uiState.value = _uiState.value.copy(
            inputText = text,
            saveStatus = SaveStatus.Idle,
            errorMessage = null
        )
    }

    fun saveMessage() {
        val text = _uiState.value.inputText
        if (text.isBlank()) {
            _uiState.value = _uiState.value.copy(
                saveStatus = SaveStatus.Error("El mensaje no puede estar vacío")
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(saveStatus = SaveStatus.Saving)

            messageRepository.add(userId, text).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        inputText = "",
                        saveStatus = SaveStatus.Success
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        saveStatus = SaveStatus.Error(error.message ?: "Error al guardar")
                    )
                }
            )
        }
    }

    fun clearStatus() {
        _uiState.value = _uiState.value.copy(saveStatus = SaveStatus.Idle)
    }
}
