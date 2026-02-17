package com.mytask.presentation.googlesheetsconfiguration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import org.koin.compose.viewmodel.koinViewModel

import com.mytask.core.presentation.screens.GenericFormScreen
import com.mytask.core.presentation.screens.FormField
import com.mytask.core.presentation.screens.FieldType
import com.mytask.core.presentation.components.ErrorContent
import androidx.compose.material3.MaterialTheme

@Composable
fun SheetUrlConfigScreen(
    viewModel: SheetUrlConfigViewModel = koinViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    when (val state = uiState) {
        is SheetUrlConfigUiState.Loading -> CircularProgressIndicator()
        is SheetUrlConfigUiState.Success -> {
            var currentUrl = state.currentUrl ?: ""
            var urlFieldError: String? = null
            
            GenericFormScreen(
                title = "Data Source",
                fields = listOf(
                    FormField(
                        key = "sheet_url",
                        label = "Google Sheets URL",
                        value = currentUrl,
                        type = FieldType.Text,
                        required = true,
                        placeholder = "Enter your Google Sheets URL",
                        error = urlFieldError
                    )
                ),
                onFieldChange = { key, value ->
                    if(key == "sheet_url") {
                        currentUrl = value
                    }
                },
                onSubmit = {
                    viewModel.updateSheetUrl(currentUrl)
                },
                onBackClick = onBackClick,
                isSubmitting = state.isValidating,
                submitText = "Save",
                headerContent = {
                    if(state.validationMessage != null) {
                        androidx.compose.material3.Text(
                            text = state.validationMessage!!,
                            color = if(state.isValidating) androidx.compose.material3.MaterialTheme.colorScheme.primary 
                                   else androidx.compose.material3.MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
        is SheetUrlConfigUiState.Error -> ErrorContent(
            title = "Error",
            message = state.message,
            onRetry = { viewModel.loadCurrentConfig() }
        )
    }
}