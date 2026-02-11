package cash.money.calculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationNavBar(
    title: String,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.width(200.dp)
            ) {
                MenuItemWithIcon(
                    icon = Icons.Default.Info,
                    text = "About",
                    onClick = {
                        showMenu = false
                        showAboutDialog = true
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 0.5.dp
                )

                MenuItemWithIcon(
                    icon = Icons.Default.Email,
                    text = "Contact Us",
                    onClick = {
                        showMenu = false
                        showContactDialog = true
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 0.5.dp
                )

                MenuItemWithIcon(
                    icon = Icons.Default.Help,
                    text = "Help",
                    onClick = {
                        showMenu = false
                        showHelpDialog = true
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 0.5.dp
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier
    )

    // Dialogs - Renamed to avoid conflict with Material3 AlertDialog
    if (showAboutDialog) {
        InfoDialog(
            title = "About Cash Calculator",
            message = "This app helps you calculate cash denominations quickly and accurately.\n\n" +
                    "Features:\n" +
                    "• Add multiple denominations\n" +
                    "• Auto-calculate totals\n" +
                    "• Save calculations\n" +
                    "• Material Design 3 interface\n\n" +
                    "Version 1.0.0",
            icon = Icons.Default.Info,
            onDismiss = { showAboutDialog = false }
        )
    }

    if (showContactDialog) {
        InfoDialog(
            title = "Contact Us",
            message = "We'd love to hear from you!\n\n" +
                    "Email: devanshsahu7000@gmail.com\n\n" +
                    "For bug reports, feature requests, or general feedback, please reach out to us.\n\n" +
                    "We typically respond within 24-48 hours.",
            icon = Icons.Default.Email,
            onDismiss = { showContactDialog = false }
        )
    }

    if (showHelpDialog) {
        InfoDialog(
            title = "How to Use",
            message = "Getting started is easy!\n\n" +
                    "1. Tap 'Add' to add denomination amounts\n" +
                    "2. Enter the note/coin value and quantity\n" +
                    "3. Watch the total calculate automatically\n" +
                    "4. Tap 'Save' to store your calculation\n\n" +
                    "Tip: Swipe left on any item to delete it!",
            icon = Icons.Default.Help,
            onDismiss = { showHelpDialog = false }
        )
    }

    if (showSettingsDialog) {
        InfoDialog(
            title = "Settings",
            message = "Settings features coming soon!\n\n" +
                    "Planned features:\n" +
                    "• Dark/Light theme toggle\n" +
                    "• Currency selection\n" +
                    "• Export calculations\n" +
                    "• Backup & Restore\n\n" +
                    "Stay tuned for updates!",
            icon = Icons.Default.Settings,
            onDismiss = { showSettingsDialog = false }
        )
    }
}

@Composable
private fun MenuItemWithIcon(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        onClick = onClick
    )
}

// Renamed from AlertDialog to InfoDialog to avoid conflict with Material3's AlertDialog
@Composable
private fun InfoDialog(
    title: String,
    message: String,
    icon: ImageVector,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Close",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    )
}
