package cash.money.calculator.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cash.money.calculator.data.PreferenceManager

@Composable
fun DenominationsManagementDialog(
    enabledDenominations: List<Int>,
    onDismiss: () -> Unit,
    onSave: (List<Int>) -> Unit
) {
    val defaultValues = listOf(2000, 500, 200, 100, 50, 20, 10, 5, 2, 1)
    var customDenominations by remember { 
        mutableStateOf(enabledDenominations.filter { it !in defaultValues }) 
    }
    
    val allFixedDenominations = defaultValues
    var selectedList by remember { mutableStateOf(enabledDenominations.toSet()) }
    
    var customValueInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Denominations Management",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Toggle denominations to show/hide them on the main screen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        Text(
                            "Fixed Denominations",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(allFixedDenominations) { value ->
                        DenominationToggleRow(
                            value = value,
                            isSelected = selectedList.contains(value),
                            onToggle = { checked ->
                                selectedList = if (checked) selectedList + value else selectedList - value
                            }
                        )
                    }
                    
                    if (customDenominations.isNotEmpty()) {
                        item {
                            Text(
                                "Custom Denominations",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(customDenominations) { value ->
                            DenominationToggleRow(
                                value = value,
                                isSelected = selectedList.contains(value),
                                onToggle = { checked ->
                                    selectedList = if (checked) selectedList + value else selectedList - value
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Add Custom Section
                Text(
                    "Add Custom Denomination",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customValueInput,
                        onValueChange = { 
                            customValueInput = it
                            errorMessage = null
                        },
                        placeholder = { Text("Value (e.g. 75)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        isError = errorMessage != null,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Button(
                        onClick = {
                            val value = customValueInput.toIntOrNull()
                            when {
                                value == null || value <= 0 -> errorMessage = "Invalid"
                                (allFixedDenominations + customDenominations).contains(value) -> errorMessage = "Exists"
                                else -> {
                                    customDenominations = (customDenominations + value).sortedByDescending { it }
                                    selectedList = selectedList + value
                                    customValueInput = ""
                                }
                            }
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Add")
                    }
                }
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedList.toList().sortedByDescending { it }) },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun DenominationToggleRow(
    value: Int,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "₹ $value",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Switch(
            checked = isSelected,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

