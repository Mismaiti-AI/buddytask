package com.mytask.presentation.googlesheetsconfiguration

// Framework
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Domain model (from Phase 2):
import com.mytask.domain.model.AppConfig

// Repository (from Phase 3):
import com.mytask.data.repository.appconfig.AppConfigRepository

// OR use cases (from Phase 4) if used:
import com.mytask.domain.usecase.GetCurrentSheetConfigUseCase
import com.mytask.domain.usecase.UpdateGoogleSheetUrlUseCase
import com.mytask.domain.usecase.ValidateSheetUrlUseCase

sealed interface SheetUrlConfigUiState {
    data object Loading : SheetUrlConfigUiState
    data class Success(val currentUrl: String?, val isValidating: Boolean, val validationMessage: String?) : SheetUrlConfigUiState
    data class Error(val message: String) : SheetUrlConfigUiState
}

class SheetUrlConfigViewModel(
    private val configRepository: AppConfigRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SheetUrlConfigUiState>(SheetUrlConfigUiState.Loading)
    val uiState: StateFlow<SheetUrlConfigUiState> = _uiState.asStateFlow()

    init {
        loadCurrentConfig()
    }

    fun loadCurrentConfig() {
        viewModelScope.launch {
            _uiState.value = SheetUrlConfigUiState.Loading
            try {
                val config = configRepository.getCurrent()
                _uiState.value = SheetUrlConfigUiState.Success(
                    currentUrl = config?.sheetUrl,
                    isValidating = false,
                    validationMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = SheetUrlConfigUiState.Error(e.message ?: "Failed to load configuration")
            }
        }
    }

    fun updateSheetUrl(url: String) {
        viewModelScope.launch {
            try {
                configRepository.updateUrl(url)
                _uiState.value = SheetUrlConfigUiState.Success(
                    currentUrl = url,
                    isValidating = false,
                    validationMessage = "URL updated successfully"
                )
            } catch (e: Exception) {
                _uiState.value = SheetUrlConfigUiState.Error(e.message ?: "Failed to update URL")
            }
        }
    }

    fun validateUrl(url: String) {
        viewModelScope.launch {
            _uiState.value = SheetUrlConfigUiState.Success(
                currentUrl = (uiState.value as? SheetUrlConfigUiState.Success)?.currentUrl,
                isValidating = true,
                validationMessage = "Validating..."
            )
            
            try {
                val isValid = configRepository.validateUrl(url)
                val message = if (isValid) "URL is valid and accessible" else "URL is not accessible"
                _uiState.value = SheetUrlConfigUiState.Success(
                    currentUrl = url,
                    isValidating = false,
                    validationMessage = message
                )
            } catch (e: Exception) {
                _uiState.value = SheetUrlConfigUiState.Success(
                    currentUrl = url,
                    isValidating = false,
                    validationMessage = "Validation failed: ${e.message}"
                )
            }
        }
    }
}