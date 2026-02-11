package cash.money.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cash.money.calculator.data.HistoryManager
import cash.money.calculator.data.PreferenceManager
import cash.money.calculator.ui.components.DenominationsManagementDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashCalculatorScreen(
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefManager = remember { PreferenceManager(context) }
    val historyManager = remember { HistoryManager(context) }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var showManagementDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    // New Dialogs for About/Contact
    var showAboutDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }

    var enabledValues by remember { mutableStateOf(prefManager.getEnabledDenominations()) }
    var selectedDenominations by remember(enabledValues) {
        mutableStateOf(enabledValues.map { DenominationItem(it, 0) })
    }

    val totalAmount = selectedDenominations.sumOf { it.value * it.quantity }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerTonalElevation = 4.dp
            ) {
                Spacer(Modifier.height(24.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        "Cash Calculator",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.History, null) },
                    label = { Text("Saved History") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToHistory()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("Denominations Management") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showManagementDialog = true
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Info, null) },
                    label = { Text("About Us") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showAboutDialog = true
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Email, null) },
                    label = { Text("Contact Us") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showContactDialog = true
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "Cash Count",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Custom Denomination"
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    bottom = paddingValues.calculateBottomPadding() + 88.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    TotalCard(
                        total = totalAmount,
                        onSave = { showSaveDialog = true },
                        onReset = {
                            selectedDenominations = selectedDenominations.map {
                                it.copy(quantity = 0)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                items(
                    items = selectedDenominations,
                    key = { it.value }
                ) { item ->
                    DenominationCardWithInput(
                        item = item,
                        onQuantityChange = { newQuantity ->
                            selectedDenominations = selectedDenominations.map {
                                if (it.value == item.value) it.copy(quantity = newQuantity)
                                else it
                            }
                        },
                        onDelete = if (!isDefaultDenomination(item.value)) {
                            {
                                selectedDenominations = selectedDenominations.filter {
                                    it.value != item.value
                                }
                            }
                        } else null
                    )
                }
            }
        }
    }

    if (showManagementDialog) {
        DenominationsManagementDialog(
            enabledDenominations = enabledValues,
            onDismiss = { showManagementDialog = false },
            onSave = { newValues ->
                prefManager.saveEnabledDenominations(newValues)
                enabledValues = newValues
                showManagementDialog = false
            }
        )
    }

    if (showSaveDialog) {
        SaveCalculationDialog(
            totalAmount = totalAmount,
            onDismiss = { showSaveDialog = false },
            onSave = { note ->
                val calculation = CalculationHistory(
                    total = totalAmount,
                    denominations = selectedDenominations.filter { it.quantity > 0 },
                    note = note
                )
                historyManager.saveCalculation(calculation)
                showSaveDialog = false
                selectedDenominations = selectedDenominations.map { it.copy(quantity = 0) }
                // Show success snackbar or toast? Just close for now.
            }
        )
    }

    if (showAddDialog) {
        AddCustomDenominationDialog(
            existingValues = selectedDenominations.map { it.value },
            onDismiss = { showAddDialog = false },
            onAdd = { newValue ->
                selectedDenominations = (selectedDenominations + DenominationItem(newValue, 0))
                    .sortedByDescending { it.value }
                showAddDialog = false
            }
        )
    }
    
    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About Us", fontWeight = FontWeight.Bold) },
            text = {
                Text("Cash Calculator is a professional tool designed for quick and accurate cash counting. Perfect for businesses and individuals.")
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("Close") }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Contact Dialog
    if (showContactDialog) {
        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = { Text("Contact Us", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("We'd love to hear from you!")
                    Spacer(Modifier.height(8.dp))
                    Text("Email: devanshsahu7000@gmail.com", fontWeight = FontWeight.Medium)
                }
            },
            confirmButton = {
                TextButton(onClick = { showContactDialog = false }) { Text("Close") }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}


// Check if denomination is default
private fun isDefaultDenomination(value: Int): Boolean {
    return value in listOf(2000, 500, 200, 100, 50, 20, 10, 5, 2, 1)
}

data class DenominationItem(
    val value: Int,
    val quantity: Int = 0
)

@Composable
private fun TotalCard(
    total: Int,
    onSave: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Amount",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "₹ $total",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Action Buttons Row
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                // Save Button
                Button(
                    onClick = onSave,
                    enabled = total > 0,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save")
                }

                // Reset Button
                OutlinedButton(
                    onClick = onReset,
                    enabled = total > 0,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset")
                }
            }
        }
    }
}

@Composable
private fun DenominationCardWithInput(
    item: DenominationItem,
    onQuantityChange: (Int) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var inputValue by remember(item.quantity) {
        mutableStateOf(if (item.quantity == 0) "" else item.quantity.toString())
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.quantity > 0)
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Denomination Value
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CurrencyRupee,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${item.value}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    )
                }
                if (item.quantity > 0) {
                    Text(
                        text = "Total: ₹ ${item.value * item.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }

            // Quantity Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Decrease button
                IconButton(
                    onClick = {
                        if (item.quantity > 0) {
                            val newQty = item.quantity - 1
                            inputValue = if (newQty == 0) "" else newQty.toString()
                            onQuantityChange(newQty)
                        }
                    },
                    enabled = item.quantity > 0,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Decrease",
                        modifier = Modifier.size(20.dp),
                        tint = if (item.quantity > 0)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline
                    )
                }

                // Input Box
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty()) {
                            inputValue = ""
                            onQuantityChange(0)
                        } else {
                            val quantity = newValue.filter { it.isDigit() }.toIntOrNull()
                            if (quantity != null && quantity >= 0 && quantity <= 9999) {
                                inputValue = quantity.toString()
                                onQuantityChange(quantity)
                            }
                        }
                    },
                    modifier = Modifier.width(70.dp),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    ),
                    placeholder = {
                        Text(
                            text = "0",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                // Increase button
                IconButton(
                    onClick = {
                        val newQty = item.quantity + 1
                        inputValue = newQty.toString()
                        onQuantityChange(newQty)
                    },
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Increase",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Delete button (only for custom denominations)
                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SaveCalculationDialog(
    totalAmount: Int,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var note by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Save,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Save Calculation",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Amount display
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Amount:",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "₹ $totalAmount",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = "Please add a note to save this calculation 📝",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = {
                        note = it
                        showError = false
                    },
                    label = { Text("Note *") },
                    placeholder = { Text("e.g., Daily cash count, Sales collection...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Notes,
                            contentDescription = null
                        )
                    },

                    isError = showError,
                    supportingText = if (showError) {
                        {
                            Text(
                                text = "Please enter a note to continue",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else null,
                    minLines = 2,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (note.trim().isEmpty()) {
                        showError = true
                    } else {
                        onSave(note.trim())
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun AddCustomDenominationDialog(
    existingValues: List<Int>,
    onDismiss: () -> Unit,
    onAdd: (Int) -> Unit
) {
    var customValue by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Add Custom Denomination",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Enter a custom denomination value that's not in the default list.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = customValue,
                    onValueChange = {
                        customValue = it
                        errorMessage = null
                    },
                    label = { Text("Denomination Value") },
                    placeholder = { Text("e.g., 75, 250") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CurrencyRupee,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = customValue.toIntOrNull()
                    when {
                        value == null || value <= 0 -> {
                            errorMessage = "Please enter a valid positive number"
                        }
                        value in existingValues -> {
                            errorMessage = "This denomination already exists"
                        }
                        else -> {
                            onAdd(value)
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
