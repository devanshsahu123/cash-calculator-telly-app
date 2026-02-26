package cash.money.calculator.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
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
    onThemeChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefManager = remember { PreferenceManager(context) }
    val historyManager = remember { HistoryManager(context) }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var showManagementDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    // Theme state for the drawer UI
    var currentTheme by remember { mutableStateOf(prefManager.getAppTheme()) }

    // New Dialogs for About/Contact
    var showAboutDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }

    var enabledValues by remember { mutableStateOf(prefManager.getEnabledDenominations()) }
    var selectedDenominations by remember(enabledValues) {
        mutableStateOf(enabledValues.map { DenominationItem(it, 0L) })
    }

    val totalAmount = selectedDenominations.sumOf { it.value.toLong() * it.quantity }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        modifier = Modifier.width(300.dp),
                        drawerContainerColor = MaterialTheme.colorScheme.surface,
                        drawerTonalElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(top = 32.dp, bottom = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(
                                    "Cash Calculator",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = (-0.5).sp
                                )
                                Text(
                                    "Professional Calculator",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

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
                            label = { Text("Denominations Settings") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                showManagementDialog = true
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp))

                        // Theme Management Section
                        Text(
                            "Theme Mode",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )

                        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                            // label → stored key mapping
                            listOf(
                                Triple("Default", "System", Icons.Default.Brightness6),
                                Triple("Light",   "Light",  Icons.Default.LightMode),
                                Triple("Dark",    "Dark",   Icons.Default.DarkMode)
                            ).forEach { (label, key, icon) ->
                                NavigationDrawerItem(
                                    icon = {
                                        Icon(imageVector = icon, contentDescription = null)
                                    },
                                    label = {
                                        Column {
                                            Text(label)
                                            if (label == "Default") {
                                                Text(
                                                    "Follows device theme",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    },
                                    selected = currentTheme == key,
                                    onClick = {
                                        currentTheme = key
                                        onThemeChange(key)
                                    },
                                    modifier = Modifier.height(if (label == "Default") 56.dp else 48.dp)
                                )
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Info, null) },
                            label = { Text("About") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                showAboutDialog = true
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Email, null) },
                            label = { Text("Support") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                showContactDialog = true
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.AccountBalanceWallet,
                                        null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Cash Calculator",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.onSurface,
                                actionIconContentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            windowInsets = TopAppBarDefaults.windowInsets
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    },
                    bottomBar = {
                        Column {
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 16.dp)
                                        .navigationBarsPadding(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    TextButton(
                                        onClick = {
                                            if (totalAmount > 0) showResetConfirmDialog = true
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        enabled = totalAmount > 0,
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    ) {
                                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Reset", fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                                    }

                                    Button(
                                        onClick = { showSaveDialog = true },
                                        modifier = Modifier
                                            .weight(1.2f)
                                            .height(52.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        enabled = totalAmount > 0,
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 4.dp,
                                            pressedElevation = 2.dp
                                        ),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    ) {
                                        Icon(Icons.Default.Save, null, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(10.dp))
                                        Text("Save Report", fontWeight = FontWeight.ExtraBold, letterSpacing = 0.5.sp)
                                    }
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        // Compact Total Banner
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total Amount",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "₹ $totalAmount",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
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
                                    onLockToggle = {
                                        selectedDenominations = selectedDenominations.map {
                                            if (it.value == item.value) it.copy(isLocked = !it.isLocked)
                                            else it
                                        }
                                    },  
                                    onDelete = if (!isDefaultDenomination(item.value)) {
                                        {
                                            val newEnabled = enabledValues.filter { it != item.value }
                                            prefManager.saveEnabledDenominations(newEnabled)
                                            enabledValues = newEnabled
                                        }
                                    } else null
                                )
                            }
                        }
                    }
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

    // Reset Confirmation Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            icon = {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text("Reset All Values?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "This will clear all denomination quantities. This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedDenominations = selectedDenominations.map { it.copy(quantity = 0L, isLocked = false) }
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Reset", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showResetConfirmDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
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
                selectedDenominations = selectedDenominations.map { it.copy(quantity = 0L, isLocked = false) }
            }
        )
    }

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


private fun isDefaultDenomination(value: Int): Boolean {
    return value in listOf(2000, 500, 200, 100, 50, 20, 10, 5, 2, 1)
}

data class DenominationItem(
    val value: Int,
    val quantity: Long = 0L,
    val isLocked: Boolean = false
)

@Composable
private fun DenominationCardWithInput(
    item: DenominationItem,
    onQuantityChange: (Long) -> Unit,
    onLockToggle: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var inputValue by remember(item.quantity) {
        mutableStateOf(if (item.quantity == 0L) "" else item.quantity.toString())
    }

    val isLocked = item.isLocked
    val subtotal = item.value.toLong() * item.quantity

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = when {
            isLocked -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.08f)
            item.quantity > 0 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
            else -> MaterialTheme.colorScheme.surface
        },
        shape = RoundedCornerShape(12.dp),
        border = when {
            isLocked -> BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f))
            item.quantity > 0 -> BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
            else -> null
        },
        shadowElevation = if (item.quantity > 0 || isLocked) 0.dp else 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ─── Left: denomination value + subtotal ───
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CurrencyRupee,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${item.value}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (item.quantity > 0) {
                    Text(
                        text = "₹ $subtotal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ─── Right: − | Input | + | Lock | (Delete) ───
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Decrement
                IconButton(
                    onClick = {
                        if (!isLocked && item.quantity > 0) {
                            val newQty = item.quantity - 1
                            inputValue = if (newQty == 0L) "" else newQty.toString()
                            onQuantityChange(newQty)
                        }
                    },
                    enabled = item.quantity > 0 && !isLocked,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = "Decrease",
                        modifier = Modifier.size(20.dp),
                        tint = if (item.quantity > 0 && !isLocked)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }

                // Quantity text field
                TextField(
                    value = inputValue,
                    onValueChange = { newValue ->
                        if (!isLocked) {
                            if (newValue.isEmpty()) {
                                inputValue = ""
                                onQuantityChange(0L)
                            } else {
                                // Allow any positive number (no upper cap)
                                val filtered = newValue.filter { it.isDigit() }
                                val qty = filtered.toLongOrNull()
                                if (qty != null && qty >= 0) {
                                    inputValue = qty.toString()
                                    onQuantityChange(qty)
                                }
                            }
                        }
                    },
                    readOnly = isLocked,
                    modifier = Modifier.width(80.dp),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    ),
                    placeholder = {
                        Text(
                            text = "0",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                // Increment
                IconButton(
                    onClick = {
                        if (!isLocked) {
                            val newQty = item.quantity + 1
                            inputValue = newQty.toString()
                            onQuantityChange(newQty)
                        }
                    },
                    enabled = !isLocked,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Increase",
                        modifier = Modifier.size(20.dp),
                        tint = if (!isLocked)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }

                // Lock button (after +)
                IconButton(
                    onClick = onLockToggle,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = if (isLocked) "Unlock row" else "Lock row",
                        modifier = Modifier.size(18.dp),
                        tint = if (isLocked)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                }

                // Delete (non-default denominations only)
                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove denomination",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SaveCalculationDialog(
    totalAmount: Long,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var note by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(20.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ── Header icon ──
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // ── Title + subtitle ──
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Save Record",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Secure your current calculation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // ── Amount display ──
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Total Amount",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = "₹ $totalAmount",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // ── Note input ──
                OutlinedTextField(
                    value = note,
                    onValueChange = {
                        note = it
                        showError = false
                    },
                    label = { Text("Reference Note") },
                    placeholder = { Text("e.g. Morning Collection, Sales…") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Notes,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    isError = showError,
                    supportingText = if (showError) {
                        { Text("A note is required for identification", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    minLines = 2,
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )

                // ── Action buttons ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    ) {
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = {
                            if (note.trim().isEmpty()) showError = true
                            else onSave(note.trim())
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Save Now", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}
