package com.mytask.presentation.googlesheetsconfiguration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.DataThresholding
import org.koin.compose.viewmodel.koinViewModel

import com.mytask.core.presentation.screens.GenericSettingsScreen
import com.mytask.core.presentation.screens.SettingsSection
import com.mytask.core.presentation.screens.SettingsItem
import com.mytask.core.presentation.components.SwitchRow
import com.mytask.core.presentation.components.ErrorContent

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onBackClick: () -> Unit = {},
    onSheetUrlConfigClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    when (val state = uiState) {
        is SettingsUiState.Loading -> CircularProgressIndicator()
        is SettingsUiState.Success -> {
            GenericSettingsScreen(
                title = "Settings",
                sections = state.sections.map { section ->
                    SettingsSection(
                        title = section.title,
                        items = section.settings.map { setting ->
                            when(setting.type) {
                                SettingType.TOGGLE -> SettingsItem.Toggle(
                                    title = setting.title,
                                    checked = setting.value == "true",
                                    onCheckedChange = { newValue ->
                                        viewModel.updateSetting(setting.key, newValue.toString())
                                    }
                                )
                                SettingType.NAVIGATION -> SettingsItem.Navigation(
                                    title = setting.title,
                                    subtitle = setting.value,
                                    icon = Icons.Default.DataThresholding,
                                    onClick = onSheetUrlConfigClick
                                )
                                else -> SettingsItem.Info(
                                    title = setting.title,
                                    value = setting.value
                                )
                            }
                        }
                    )
                },
                onBackClick = onBackClick
            )
        }
        is SettingsUiState.Error -> ErrorContent(
            title = "Error",
            message = state.message,
            onRetry = { viewModel.loadSettings() }
        )
    }
}