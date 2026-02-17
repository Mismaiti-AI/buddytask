package com.mismaiti.core.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.mismaiti.core.presentation.components.LoadingButton
import com.mismaiti.core.presentation.components.SecondaryButton

/**
 * Generic Form Screen - Pre-Built Template Component
 *
 * Works with ANY form data through field definitions.
 * Supports: Text, Number, Email, Phone, Password, MultiLine,
 *           Dropdown, Checkbox, RadioGroup, Date.
 *
 * Usage:
 * ```
 * GenericFormScreen(
 *     title = "Add Product",
 *     fields = listOf(
 *         FormField(key = "name", label = "Product Name", value = name, required = true),
 *         FormField(key = "price", label = "Price", value = price, type = FieldType.Number),
 *         FormField(
 *             key = "category", label = "Category", value = category,
 *             type = FieldType.Dropdown,
 *             options = listOf("Electronics", "Clothing", "Food", "Books")
 *         ),
 *         FormField(key = "date", label = "Release Date", value = date, type = FieldType.Date),
 *         FormField(
 *             key = "priority", label = "Priority", value = priority,
 *             type = FieldType.RadioGroup,
 *             options = listOf("Low", "Medium", "High")
 *         ),
 *         FormField(key = "active", label = "Active", value = "true", type = FieldType.Checkbox),
 *         FormField(key = "description", label = "Description", value = desc, type = FieldType.MultiLine),
 *     ),
 *     onFieldChange = { key, value -> viewModel.updateField(key, value) },
 *     onSubmit = { viewModel.save() },
 *     onBackClick = { navController.popBackStack() }
 * )
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericFormScreen(
    title: String,
    fields: List<FormField>,
    onFieldChange: (key: String, value: String) -> Unit,
    onSubmit: () -> Unit,
    onBackClick: () -> Unit = {},
    isSubmitting: Boolean = false,
    submitText: String = "Save",
    cancelText: String = "Cancel",
    showCancel: Boolean = true,
    headerContent: (@Composable () -> Unit)? = null,
    footerContent: (@Composable () -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.Close, "Close")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Optional header
            if (headerContent != null) {
                headerContent()
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Form Fields
            fields.forEach { field ->
                FormFieldInput(
                    field = field,
                    onValueChange = { onFieldChange(field.key, it) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Optional footer
            if (footerContent != null) {
                footerContent()
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (showCancel) {
                    SecondaryButton(
                        text = cancelText,
                        onClick = onBackClick,
                        modifier = Modifier.weight(1f),
                        enabled = !isSubmitting
                    )
                }
                LoadingButton(
                    text = submitText,
                    onClick = onSubmit,
                    modifier = Modifier.weight(1f),
                    isLoading = isSubmitting,
                    enabled = fields
                        .filter { it.required }
                        .all { it.value.isNotBlank() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormFieldInput(
    field: FormField,
    onValueChange: (String) -> Unit
) {
    when (field.type) {
        FieldType.Dropdown -> DropdownField(
            field,
            onValueChange
        )
        FieldType.Checkbox -> CheckboxField(
            field,
            onValueChange
        )
        FieldType.RadioGroup -> RadioGroupField(
            field,
            onValueChange
        )
        FieldType.Date -> DateField(
            field,
            onValueChange
        )
        else -> TextInputField(
            field,
            onValueChange
        )
    }
}

@Composable
private fun TextInputField(
    field: FormField,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = field.value,
        onValueChange = onValueChange,
        label = {
            Text(if (field.required) "${field.label} *" else field.label)
        },
        placeholder = field.placeholder?.let { { Text(it) } },
        modifier = Modifier.fillMaxWidth(),
        enabled = field.enabled,
        readOnly = field.readOnly,
        singleLine = field.type != FieldType.MultiLine,
        minLines = if (field.type == FieldType.MultiLine) 3 else 1,
        maxLines = if (field.type == FieldType.MultiLine) 6 else 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = when (field.type) {
                FieldType.Number -> KeyboardType.Number
                FieldType.Email -> KeyboardType.Email
                FieldType.Phone -> KeyboardType.Phone
                FieldType.Password -> KeyboardType.Password
                else -> KeyboardType.Text
            }
        ),
        visualTransformation = if (field.type == FieldType.Password) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        isError = field.error != null,
        supportingText = field.error?.let {
            {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        shape = MaterialTheme.shapes.medium
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    field: FormField,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (field.enabled) expanded = it }
    ) {
        OutlinedTextField(
            value = field.value,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(if (field.required) "${field.label} *" else field.label)
            },
            placeholder = field.placeholder?.let { { Text(it) } },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
            enabled = field.enabled,
            isError = field.error != null,
            supportingText = field.error?.let {
                {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            shape = MaterialTheme.shapes.medium
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            field.options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CheckboxField(
    field: FormField,
    onValueChange: (String) -> Unit
) {
    val checked = field.value.equals("true", ignoreCase = true)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = field.enabled) {
                    onValueChange((!checked).toString())
                }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { onValueChange(it.toString()) },
                enabled = field.enabled
            )
            Text(
                text = if (field.required) "${field.label} *" else field.label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (field.enabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        if (field.error != null) {
            Text(
                text = field.error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
private fun RadioGroupField(
    field: FormField,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = if (field.required) "${field.label} *" else field.label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        field.options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = field.enabled) { onValueChange(option) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = field.value == option,
                    onClick = { onValueChange(option) },
                    enabled = field.enabled
                )
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (field.enabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        if (field.error != null) {
            Text(
                text = field.error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
private fun DateField(
    field: FormField,
    onValueChange: (String) -> Unit
) {
    // Date is rendered as a read-only text field.
    // The caller handles showing a date picker dialog and passing the result via onFieldChange.
    OutlinedTextField(
        value = field.value,
        onValueChange = {},
        readOnly = true,
        label = {
            Text(if (field.required) "${field.label} *" else field.label)
        },
        placeholder = field.placeholder?.let { { Text(it) } }
            ?: { Text("Select date") },
        trailingIcon = {
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select date")
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = field.enabled,
        isError = field.error != null,
        supportingText = field.error?.let {
            {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        shape = MaterialTheme.shapes.medium
    )
}

data class FormField(
    val key: String,
    val label: String,
    val value: String,
    val type: FieldType = FieldType.Text,
    val required: Boolean = false,
    val enabled: Boolean = true,
    val readOnly: Boolean = false,
    val placeholder: String? = null,
    val error: String? = null,
    val options: List<String> = emptyList()
)

enum class FieldType {
    Text,
    Number,
    Email,
    Phone,
    Password,
    MultiLine,
    Dropdown,
    Checkbox,
    RadioGroup,
    Date
}
