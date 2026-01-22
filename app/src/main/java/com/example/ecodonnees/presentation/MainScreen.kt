package com.example.ecodonnees.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToPackages: () -> Unit
) {
    val dataStatus by viewModel.dataStatus.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val context = LocalContext.current

    var showQuotaDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EcoDonnées") },
                actions = {
                    IconButton(onClick = { showThemeDialog = true }) {
                        Icon(Icons.Default.Palette, "Thème")
                    }
                    IconButton(onClick = { showQuotaDialog = true }) {
                        Icon(Icons.Default.Settings, "Paramètres")
                    }
                }
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
            dataStatus?.let { status ->
                StatusCard(status)
                UsageCard(status)
                ActionsCard(status, viewModel, onNavigateToPackages)
            } ?: LoadingCard()

            Spacer(Modifier.weight(1f))

            Text(
                text = "© 2026 AmanEntreprise",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://amanentreprise.page.gd/"))
                        context.startActivity(intent)
                    }
                    .padding(vertical = 8.dp)
            )
        }
    }

    if (showQuotaDialog) {
        QuotaDialog(
            viewModel = viewModel,
            onDismiss = { showQuotaDialog = false }
        )
    }

    if (showThemeDialog) {
        ThemeDialog(
            currentMode = themeMode,
            onSelect = { viewModel.setThemeMode(it) },
            onDismiss = { showThemeDialog = false }
        )
    }
}

@Composable
fun StatusCard(status: com.example.ecodonnees.domain.model.DataStatus) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("État Internet", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    if (status.isBlocked) "🔴 Bloqué" else "🟢 Autorisé",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "VPN: ${if (status.isVpnActive) "Actif" else "Inactif"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun UsageCard(status: com.example.ecodonnees.domain.model.DataStatus) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Consommation", style = MaterialTheme.typography.titleMedium)

            LinearProgressIndicator(
                progress = (status.percentageUsed / 100f).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Utilisé", style = MaterialTheme.typography.labelSmall)
                    Text(
                        "${status.usedBytes / (1024 * 1024)} MB",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Quota", style = MaterialTheme.typography.labelSmall)
                    Text(
                        "${status.quotaBytes / (1024 * 1024)} MB",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Restant", style = MaterialTheme.typography.labelSmall)
                    Text(
                        "${status.remainingBytes / (1024 * 1024)} MB",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Pourcentage", style = MaterialTheme.typography.labelSmall)
                    Text(
                        "${status.percentageUsed.toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Divider()

            val expiryDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(Date(status.expiryTimestamp))
            Text("Expiration: $expiryDate", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ActionsCard(
    status: com.example.ecodonnees.domain.model.DataStatus,
    viewModel: MainViewModel,
    onNavigateToPackages: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Actions", style = MaterialTheme.typography.titleMedium)

            Button(
                onClick = onNavigateToPackages,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(Icons.Default.ShoppingCart, null)
                Spacer(Modifier.width(8.dp))
                Text("Acheter un forfait")
            }

            if (!status.isBlocked) {
                Button(
                    onClick = { viewModel.blockInternet() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Block, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Bloquer maintenant")
                }
            } else {
                Button(
                    onClick = { viewModel.unblockInternet() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.LockOpen, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Réactiver Internet")
                }
            }

            OutlinedButton(
                onClick = { viewModel.resetUsage() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, null)
                Spacer(Modifier.width(8.dp))
                Text("Réinitialiser consommation")
            }
        }
    }
}

@Composable
fun LoadingCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotaDialog(viewModel: MainViewModel, onDismiss: () -> Unit) {
    var quotaMB by remember { mutableStateOf("1000") }
    var expiryMode by remember { mutableStateOf(ExpiryMode.DAYS) }
    var expiryDays by remember { mutableStateOf("30") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    calendar.add(Calendar.DAY_OF_MONTH, 30)

    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }
    var selectedHour by remember { mutableStateOf(23) }
    var selectedMinute by remember { mutableStateOf(59) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )

    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurer le quota") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = quotaMB,
                    onValueChange = { quotaMB = it.filter { c -> c.isDigit() } },
                    label = { Text("Quota (MB)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Mode d'expiration", style = MaterialTheme.typography.labelMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = expiryMode == ExpiryMode.DAYS,
                        onClick = { expiryMode = ExpiryMode.DAYS },
                        label = { Text("Jours") },
                        leadingIcon = if (expiryMode == ExpiryMode.DAYS) {
                            { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = expiryMode == ExpiryMode.CUSTOM,
                        onClick = { expiryMode = ExpiryMode.CUSTOM },
                        label = { Text("Date/Heure") },
                        leadingIcon = if (expiryMode == ExpiryMode.CUSTOM) {
                            { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                }

                when (expiryMode) {
                    ExpiryMode.DAYS -> {
                        OutlinedTextField(
                            value = expiryDays,
                            onValueChange = { expiryDays = it.filter { c -> c.isDigit() } },
                            label = { Text("Validité (jours)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    ExpiryMode.CUSTOM -> {
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.CalendarToday, null)
                            Spacer(Modifier.width(8.dp))
                            Text(formatDate(selectedDate))
                        }

                        OutlinedButton(
                            onClick = { showTimePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Schedule, null)
                            Spacer(Modifier.width(8.dp))
                            Text(String.format("%02d:%02d", selectedHour, selectedMinute))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val quota = quotaMB.toLongOrNull() ?: 1000L
                val expiryTimestamp = when (expiryMode) {
                    ExpiryMode.DAYS -> {
                        val days = expiryDays.toLongOrNull() ?: 30L
                        System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000)
                    }
                    ExpiryMode.CUSTOM -> {
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = selectedDate
                        cal.set(Calendar.HOUR_OF_DAY, selectedHour)
                        cal.set(Calendar.MINUTE, selectedMinute)
                        cal.set(Calendar.SECOND, 0)
                        cal.set(Calendar.MILLISECOND, 0)
                        cal.timeInMillis
                    }
                }
                viewModel.saveQuotaWithTimestamp(quota * 1024 * 1024, expiryTimestamp)
                onDismiss()
            }) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDate = it }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Annuler")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedHour = timePickerState.hour
                    selectedMinute = timePickerState.minute
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Annuler")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

enum class ExpiryMode {
    DAYS, CUSTOM
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun ThemeDialog(
    currentMode: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choisir le thème") },
        text = {
            Column {
                ThemeMode.values().forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentMode == mode,
                            onClick = { onSelect(mode); onDismiss() }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            when (mode) {
                                ThemeMode.LIGHT -> "Clair"
                                ThemeMode.DARK -> "Sombre"
                                ThemeMode.AUTO -> "Automatique"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}