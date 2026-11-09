package com.example.ui.screens.mirath

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.model.Madhab
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MirathCalculatorScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.selectTab(MainTab.DASHBOARD)
    }

    val mirath = state.mirathState
    var activeTab by remember { mutableIntStateOf(0) } // 0: Estate, 1: Heirs, 2: Distribution

    // Calculation result
    val result = remember(mirath.deceasedGender, mirath.liabilities, mirath.heirs, mirath.selectedMadhab) {
        MirathCalculator.calculate(
            gender = mirath.deceasedGender,
            liabilities = mirath.liabilities,
            heirs = mirath.heirs,
            madhab = mirath.selectedMadhab
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "Inheritance Calculator",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldAccent.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Mirath",
                                    color = GoldAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Surah An-Nisa 4:11-12 • 4 Madhab Jurisprudence",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.selectTab(MainTab.DASHBOARD) },
                        modifier = Modifier.testTag("mirath_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { activeTab = 2 },
                        modifier = Modifier.testTag("mirath_results_shortcut")
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = "Calculate", tint = EmeraldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldPrimary
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("1. Estate & Debts", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("2. Heirs", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("3. Shares & Madhab", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (activeTab) {
                    0 -> {
                        // Section 1: Estate & Deductions
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Text(
                                        text = "Deceased & Estate Valuation",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    // Gender of Deceased
                                    Text("Gender of Deceased", style = MaterialTheme.typography.labelMedium)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        FilterChip(
                                            selected = mirath.deceasedGender == DeceasedGender.MALE,
                                            onClick = { viewModel.setMirathDeceasedGender(DeceasedGender.MALE) },
                                            label = { Text("Male (Father / Husband / Brother)") },
                                            leadingIcon = { Icon(Icons.Default.Male, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        FilterChip(
                                            selected = mirath.deceasedGender == DeceasedGender.FEMALE,
                                            onClick = { viewModel.setMirathDeceasedGender(DeceasedGender.FEMALE) },
                                            label = { Text("Female (Mother / Wife / Sister)") },
                                            leadingIcon = { Icon(Icons.Default.Female, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                    // Gross Estate Input
                                    OutlinedTextField(
                                        value = if (mirath.liabilities.grossEstate > 0) mirath.liabilities.grossEstate.toInt().toString() else "",
                                        onValueChange = { str ->
                                            val v = str.toDoubleOrNull() ?: 0.0
                                            viewModel.setMirathGrossEstate(v)
                                        },
                                        label = { Text("Gross Estate Value (${mirath.currencyCode})") },
                                        placeholder = { Text("Cash, property, gold total") },
                                        leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = EmeraldPrimary) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth().testTag("mirath_gross_estate_input")
                                    )

                                    // Funeral Expenses
                                    OutlinedTextField(
                                        value = if (mirath.liabilities.funeralExpenses > 0) mirath.liabilities.funeralExpenses.toInt().toString() else "",
                                        onValueChange = { str ->
                                            val v = str.toDoubleOrNull() ?: 0.0
                                            viewModel.setMirathFuneralExpenses(v)
                                        },
                                        label = { Text("Funeral & Burial Expenses (Tajheez)") },
                                        placeholder = { Text("Kafan, grave, transport") },
                                        leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // Debts Owed
                                    OutlinedTextField(
                                        value = if (mirath.liabilities.debtsOwed > 0) mirath.liabilities.debtsOwed.toInt().toString() else "",
                                        onValueChange = { str ->
                                            val v = str.toDoubleOrNull() ?: 0.0
                                            viewModel.setMirathDebtsOwed(v)
                                        },
                                        label = { Text("Debts & Unpaid Liabilities") },
                                        placeholder = { Text("Loans, unpaid Mahr, unpaid Zakat") },
                                        leadingIcon = { Icon(Icons.Default.MoneyOff, contentDescription = null) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // Wasiyyah (Bequest)
                                    OutlinedTextField(
                                        value = if (mirath.liabilities.wasiyyahBequest > 0) mirath.liabilities.wasiyyahBequest.toInt().toString() else "",
                                        onValueChange = { str ->
                                            val v = str.toDoubleOrNull() ?: 0.0
                                            viewModel.setMirathWasiyyah(v)
                                        },
                                        label = { Text("Bequest to Non-Heirs (Wasiyyah, max 1/3)") },
                                        placeholder = { Text("Charity, non-heir relatives") },
                                        leadingIcon = { Icon(Icons.Default.VolunteerActivism, contentDescription = null) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // Net Distributable Hero Summary
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = EmeraldPrimary.copy(alpha = 0.12f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Net Distributable Estate",
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = EmeraldPrimary
                                                )
                                                Text(
                                                    text = "Gross - Funeral - Debts - Wasiyyah",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Text(
                                                text = "${mirath.currencyCode} ${"%,.2f".format(mirath.liabilities.netDistributableEstate)}",
                                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                                                color = EmeraldPrimary
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { activeTab = 1 },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Next: Select Surviving Heirs →", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Section 2: Heirs Selection
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Text(
                                        text = "Surviving Heirs Setup",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    // Spouses
                                    if (mirath.deceasedGender == DeceasedGender.FEMALE) {
                                        HeirToggleRow(
                                            title = "Husband",
                                            arabic = "الزَّوْج",
                                            isActive = mirath.heirs.husbandCount > 0,
                                            onToggle = { viewModel.setMirathHusband(if (mirath.heirs.husbandCount > 0) 0 else 1) }
                                        )
                                    } else {
                                        HeirStepperRow(
                                            title = "Wives (Up to 4)",
                                            arabic = "الزَّوْجَات",
                                            count = mirath.heirs.wifeCount,
                                            min = 0,
                                            max = 4,
                                            onCountChanged = { viewModel.setMirathWives(it) }
                                        )
                                    }

                                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                    // Children
                                    HeirStepperRow(
                                        title = "Sons",
                                        arabic = "الْأَبْنَاء",
                                        count = mirath.heirs.sonsCount,
                                        min = 0,
                                        max = 12,
                                        onCountChanged = { viewModel.setMirathSons(it) }
                                    )

                                    HeirStepperRow(
                                        title = "Daughters",
                                        arabic = "الْبَنَات",
                                        count = mirath.heirs.daughtersCount,
                                        min = 0,
                                        max = 12,
                                        onCountChanged = { viewModel.setMirathDaughters(it) }
                                    )

                                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                                    // Parents
                                    HeirToggleRow(
                                        title = "Father",
                                        arabic = "الْأَب",
                                        isActive = mirath.heirs.fatherAlive,
                                        onToggle = { viewModel.setMirathFather(!mirath.heirs.fatherAlive) }
                                    )

                                    HeirToggleRow(
                                        title = "Mother",
                                        arabic = "الْأُمّ",
                                        isActive = mirath.heirs.motherAlive,
                                        onToggle = { viewModel.setMirathMother(!mirath.heirs.motherAlive) }
                                    )

                                    Button(
                                        onClick = { activeTab = 2 },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                        modifier = Modifier.fillMaxWidth().testTag("mirath_calculate_shares_button")
                                    ) {
                                        Text("Calculate Quranic Shares →", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Section 3: Shares Distribution & 4 Madhab
                        item {
                            // 4-Madhab Selector in 2x2 Mobile Grid
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "Select Fiqh School (Madhab)",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    val madhabPairs = Madhab.entries.chunked(2)
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        madhabPairs.forEach { pair ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                pair.forEach { m ->
                                                    FilterChip(
                                                        selected = mirath.selectedMadhab == m,
                                                        onClick = { viewModel.setMirathMadhab(m) },
                                                        label = {
                                                            Text(
                                                                text = m.name.lowercase().replaceFirstChar { it.uppercase() } + " Fiqh",
                                                                fontSize = 11.5.sp,
                                                                fontWeight = if (mirath.selectedMadhab == m) FontWeight.Bold else FontWeight.Medium
                                                            )
                                                        },
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .testTag("madhab_chip_${m.name}")
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Summary Hero Card
                        item {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.fillMaxWidth().testTag("mirath_result_hero")
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "TOTAL NET ESTATE",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = GoldLight
                                        )
                                        if (result.isAwlApplied) {
                                            Surface(shape = RoundedCornerShape(4.dp), color = GoldAccent) {
                                                Text("'Awl Applied", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                        } else if (result.isRaddApplied) {
                                            Surface(shape = RoundedCornerShape(4.dp), color = GoldAccent) {
                                                Text("Radd Applied", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                        }
                                    }

                                    Text(
                                        text = "${mirath.currencyCode} ${"%,.2f".format(result.netEstate)}",
                                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color.White)
                                    )

                                    Text(
                                        text = "Distributed to ${result.shares.size} heir groups across ${result.shares.sumOf { it.count }} individuals.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }
                        }

                        // Shares Breakdown List
                        items(result.shares) { share ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = GoldAccent.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = share.shareFractionString,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                                    color = GoldAccent,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            Column {
                                                Text(
                                                    text = share.heirTitle,
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                                )
                                                Text(
                                                    text = share.basisQuranVerse,
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Text(
                                            text = share.arabicTitle,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary),
                                            textAlign = TextAlign.End
                                        )
                                    }

                                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Total Group Amount",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "${mirath.currencyCode} ${"%,.2f".format(share.totalAmount)} (${"%.1f".format(share.percentage)}%)",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                            )
                                        }

                                        if (share.count > 1) {
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = "Each Person",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = "${mirath.currencyCode} ${"%,.2f".format(share.amountPerPerson)}",
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                                )
                                            }
                                        }
                                    }

                                    if (share.fiqhNote.isNotBlank()) {
                                        Text(
                                            text = share.fiqhNote,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // Fiqh & Madhab Notes Accordion
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Jurisprudence & Fiqh Notes",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    result.summaryNotes.forEach { note ->
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text("•", color = EmeraldPrimary)
                                            Text(note, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeirStepperRow(
    title: String,
    arabic: String,
    count: Int,
    min: Int,
    max: Int,
    onCountChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Text(arabic, style = MaterialTheme.typography.bodySmall, color = EmeraldPrimary)
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = { if (count > min) onCountChanged(count - 1) },
                enabled = count > min,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(18.dp))
            }

            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.widthIn(min = 28.dp),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = { if (count < max) onCountChanged(count + 1) },
                enabled = count < max,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun HeirToggleRow(
    title: String,
    arabic: String,
    isActive: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onToggle() }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Text(arabic, style = MaterialTheme.typography.bodySmall, color = EmeraldPrimary)
        }

        Switch(
            checked = isActive,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = EmeraldPrimary
            )
        )
    }
}
