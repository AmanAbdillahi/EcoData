package com.example.ecodonnees.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ecodonnees.domain.model.InternetPackage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackagePurchaseScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val packages = remember { InternetPackage.getAvailablePackages() }
    var selectedPackage by remember { mutableStateOf<InternetPackage?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var purchaseSuccess by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acheter un forfait") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Retour")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Forfaits disponibles",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
            }

            items(packages) { package_ ->
                PackageCard(
                    package_ = package_,
                    onSelect = {
                        selectedPackage = package_
                        showConfirmDialog = true
                    }
                )
            }
        }
    }

    if (showConfirmDialog && selectedPackage != null) {
        ConfirmPurchaseDialog(
            package_ = selectedPackage!!,
            onConfirm = {
                viewModel.purchasePackage(
                    package_ = selectedPackage!!,
                    onResult = { success, message ->
                        purchaseSuccess = success
                        resultMessage = message
                        showConfirmDialog = false
                        showResultDialog = true
                    }
                )
            },
            onDismiss = {
                showConfirmDialog = false
                selectedPackage = null
            }
        )
    }

    if (showResultDialog) {
        ResultDialog(
            success = purchaseSuccess,
            message = resultMessage,
            packageName = selectedPackage?.name ?: "",
            onDismiss = {
                showResultDialog = false
                selectedPackage = null
                if (purchaseSuccess) {
                    onNavigateBack()
                }
            }
        )
    }
}

@Composable
fun PackageCard(
    package_: InternetPackage,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSelect
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Forfait ${package_.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DataUsage,
                                null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${package_.dataGB} Go",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarToday,
                                null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "${package_.validityDays} jour${if (package_.validityDays > 1) "s" else ""}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        package_.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    Icons.Default.ShoppingCart,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ConfirmPurchaseDialog(
    package_: InternetPackage,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.ShoppingCart,
                null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { Text("Confirmation d'achat") },
        text = {
            Text(
                "Voulez-vous acheter le forfait ${package_.name} de ${package_.dataGB} Go, valable ${package_.validityDays} jour${if (package_.validityDays > 1) "s" else ""} ?"
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Oui")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Non")
            }
        }
    )
}

@Composable
fun ResultDialog(
    success: Boolean,
    message: String,
    packageName: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                if (success) Icons.Default.CheckCircle else Icons.Default.Error,
                null,
                tint = if (success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(if (success) "Succès" else "Erreur")
        },
        text = {
            Text(
                if (success) {
                    "Félicitations, vous avez acheté le forfait $packageName avec succès."
                } else {
                    "Désolé, une erreur s'est produite. Veuillez vérifier que votre solde est suffisant et réessayer plus tard."
                }
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}