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

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val sections: List<SettingsSection>) : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}

data class SettingsSection(
    val title: String,
    val settings: List<SettingItem>
)

data class SettingItem(
    val key: String,
    val title: String,
    val value: String,
    val type: SettingType
)

enum class SettingType {
    TOGGLE, TEXT, SELECT
}

class SettingsViewModel(
    private val configRepository: AppConfigRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = SettingsUiState.Loading
            try {
                val config = configRepository.getCurrent()
                val sections = buildSettingsSections(config)
                _uiState.value = SettingsUiState.Success(sections)
            } catch (e: Exception) {
                _uiState.value = SettingsUiState.Error(e.message ?: "Failed to load settings")
            }
        }
    }

    private fun buildSettingsSections(config: AppConfig?): List<SettingsSection> {
        return listOf(
            SettingsSection(
                title = "Appearance",
                settings = listOf(
                    SettingItem(
                        key = "dark_mode",
                        title = "Dark Mode",
                        value = "false", // This would be retrieved from a preference store
                        type = SettingType.TOGGLE
                    )
                )
            ),
            SettingsSection(
                title = "Data Sync",
                settings = listOf(
                    SettingItem(
                        key = "sheet_url",
                        title = "Google Sheet URL",
                        value = config?.sheetUrl ?: "",
                        type = SettingType.TEXT
                    )
                )
            )
        )
    }

    fun updateSetting(key: String, value: String) {
        viewModelScope.launch {
            when (key) {
                "sheet_url" -> configRepository.updateUrl(value)
                // Handle other settings as needed
            }
            loadSettings() // Refresh the UI after updating
        }
    }
}