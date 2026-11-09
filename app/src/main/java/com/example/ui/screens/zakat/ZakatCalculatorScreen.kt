package com.example.ui.screens.zakat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.viewmodel.DeenUiState
import com.example.ui.viewmodel.DeenViewModel
import com.example.ui.viewmodel.MainTab
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZakatCalculatorScreen(
    state: DeenUiState,
    viewModel: DeenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler {
        viewModel.selectTab(MainTab.DASHBOARD)
    }

    var activeTab by remember { mutableIntStateOf(0) }
    var isCurrencyPickerOpen by remember { mutableStateOf(false) }
    var isSaveDialogOpen by remember { mutableStateOf(false) }
    var isCustomRateDialogOpen by remember { mutableStateOf(false) }

    // Real-time calculated result conforming strictly to Prophetic Hadith
    val calculationResult = remember(
        state.zakatAssetInput,
        state.selectedZakatCurrency,
        state.zakatRates,
        state.selectedZakatNisabStandard,
        state.isZakatHawlMet
    ) {
        viewModel.calculateCurrentZakat()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Zakat Calculator",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "2.5% Hadith",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Live Gold & Silver Nisab • Multi-Currency",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.selectTab(MainTab.DASHBOARD) },
                        modifier = Modifier.testTag("zakat_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                },
                actions = {
                    // Currency Selector Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { isCurrencyPickerOpen = true }
                            .testTag("zakat_currency_selector_pill")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = state.selectedZakatCurrency.flagEmoji, fontSize = 14.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = state.selectedZakatCurrency.code,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Change Currency",
                                modifier = Modifier.size(18.dp),
                                tint = EmeraldPrimary
                            )
                        }
                    }

                    // Refresh Live Prices Button
                    IconButton(
                        onClick = {
                            viewModel.refreshZakatRates()
                            Toast.makeText(context, "Updating live precious metal rates...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("zakat_refresh_rates_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Live Rates",
                            tint = if (state.isRefreshingZakatRates) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("zakat_calculator_screen")
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Live Precious Metals Market Ticker Banner
            LiveMarketTickerHeader(
                rates = state.zakatRates,
                currency = state.selectedZakatCurrency,
                onCustomizeClick = { isCustomRateDialogOpen = true },
                onRefreshClick = { viewModel.refreshZakatRates() },
                isRefreshing = state.isRefreshingZakatRates
            )

            // Primary Navigation Tabs (Calculator, Hadith Proofs, 8 Beneficiaries, Saved History)
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldPrimary,
                modifier = Modifier.testTag("zakat_tab_row")
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Calculator", fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("zakat_tab_calc")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Hadith Proofs", fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("zakat_tab_hadith")
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("8 Beneficiaries", fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("zakat_tab_beneficiaries")
                )
                Tab(
                    selected = activeTab == 3,
                    onClick = { activeTab = 3 },
                    text = { Text("History (${state.savedZakatRecords.size})", fontWeight = if (activeTab == 3) FontWeight.Bold else FontWeight.Normal) },
                    icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("zakat_tab_history")
                )
            }

            // Tab Content
            when (activeTab) {
                0 -> CalculatorTabContent(
                    state = state,
                    result = calculationResult,
                    onInputChange = { updated -> viewModel.updateZakatAssetInput(updated) },
                    onNisabStandardChange = { std -> viewModel.setZakatNisabStandard(std) },
                    onHawlToggle = { met -> viewModel.setZakatHawlMet(met) },
                    onSaveClick = { isSaveDialogOpen = true },
                    onShareSummary = {
                        val text = formatZakatSummary(calculationResult, state.selectedZakatCurrency)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Deen365 Zakat Summary", text))
                        Toast.makeText(context, "Zakat summary copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                )
                1 -> HadithProofsTabContent()
                2 -> BeneficiariesTabContent()
                3 -> SavedHistoryTabContent(
                    records = state.savedZakatRecords,
                    currency = state.selectedZakatCurrency,
                    onDelete = { id -> viewModel.deleteZakatRecord(id) }
                )
            }
        }
    }

    // Currency Selection Modal
    if (isCurrencyPickerOpen) {
        CurrencyPickerModal(
            currentCurrency = state.selectedZakatCurrency,
            onSelectCurrency = { selected ->
                viewModel.selectZakatCurrency(selected.code)
                isCurrencyPickerOpen = false
            },
            onDismiss = { isCurrencyPickerOpen = false }
        )
    }

    // Save Zakat Calculation Dialog
    if (isSaveDialogOpen) {
        SaveZakatRecordDialog(
            result = calculationResult,
            currency = state.selectedZakatCurrency,
            onConfirmSave = { title, notes ->
                viewModel.saveZakatCalculation(title, notes)
                isSaveDialogOpen = false
                Toast.makeText(context, "Zakat calculation record saved", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { isSaveDialogOpen = false }
        )
    }

    // Custom Market Override Dialog
    if (isCustomRateDialogOpen) {
        CustomPreciousMetalsDialog(
            currentRates = state.zakatRates,
            currency = state.selectedZakatCurrency,
            onSaveCustom = { goldUsd, silverUsd ->
                viewModel.setCustomGoldSilverRates(goldUsd, silverUsd)
                isCustomRateDialogOpen = false
                Toast.makeText(context, "Custom bullion prices saved", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { isCustomRateDialogOpen = false }
        )
    }
}

// ==========================================
// 1. LIVE MARKET TICKER HEADER
// ==========================================
@Composable
private fun LiveMarketTickerHeader(
    rates: GoldSilverRates,
    currency: CurrencyInfo,
    onCustomizeClick: () -> Unit,
    onRefreshClick: () -> Unit,
    isRefreshing: Boolean
) {
    val gold24k = rates.getGoldPricePerGram(currency, GoldPurity.K24)
    val gold22k = rates.getGoldPricePerGram(currency, GoldPurity.K22)
    val silverPerGram = rates.getSilverPricePerGram(currency)

    val dateFormat = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }
    val formattedTime = remember(rates.lastUpdatedTimestamp) {
        dateFormat.format(Date(rates.lastUpdatedTimestamp))
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (rates.isLiveFetched) EmeraldPrimary else GoldAccent)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (rates.isLiveFetched) "Live Precious Metal Index" else "Cached Market Rates",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = " • $formattedTime",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Text(
                    text = "Edit Spot Price",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    ),
                    modifier = Modifier
                        .clickable { onCustomizeClick() }
                        .testTag("edit_spot_price_button")
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Gold 24K Card
                RateTickerCard(
                    modifier = Modifier.weight(1f),
                    title = "Gold 24K / gram",
                    symbol = "Au 24K",
                    value = "${currency.symbol} ${formatNumber(gold24k)}",
                    accentColor = GoldAccent
                )

                // Gold 22K Card
                RateTickerCard(
                    modifier = Modifier.weight(1f),
                    title = "Gold 22K / gram",
                    symbol = "Au 22K",
                    value = "${currency.symbol} ${formatNumber(gold22k)}",
                    accentColor = GoldLight
                )

                // Silver Card
                RateTickerCard(
                    modifier = Modifier.weight(1f),
                    title = "Silver / gram",
                    symbol = "Ag Pure",
                    value = "${currency.symbol} ${formatNumber(silverPerGram)}",
                    accentColor = Color(0xFF90A4AE)
                )
            }
        }
    }
}

@Composable
private fun RateTickerCard(
    title: String,
    symbol: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = symbol,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

// ==========================================
// 2. CALCULATOR TAB CONTENT
// ==========================================
@Composable
private fun CalculatorTabContent(
    state: DeenUiState,
    result: ZakatCalculationResult,
    onInputChange: (ZakatAssetInput) -> Unit,
    onNisabStandardChange: (NisabStandard) -> Unit,
    onHawlToggle: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onShareSummary: () -> Unit
) {
    val currency = state.selectedZakatCurrency

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Hero Result Card
        item {
            HeroZakatResultCard(
                result = result,
                currency = currency,
                onSaveClick = onSaveClick,
                onShareClick = onShareSummary
            )
        }

        // Nisab Standard Selector & Hawl Lunar Year Toggle
        item {
            NisabAndHawlSettingsCard(
                result = result,
                currency = currency,
                selectedStandard = state.selectedZakatNisabStandard,
                onStandardChange = onNisabStandardChange,
                isHawlMet = state.isZakatHawlMet,
                onHawlToggle = onHawlToggle
            )
        }

        // Section 1: Cash & Liquid Funds
        item {
            AssetCategoryCard(
                title = "1. Cash & Bank Balances",
                subtitle = "Currency in hand, savings, checking & foreign cash",
                icon = Icons.Default.Payments,
                totalValue = result.totalCashValue,
                currency = currency
            ) {
                NumericInputField(
                    label = "Cash in Hand & Bank Accounts (${currency.symbol})",
                    value = state.zakatAssetInput.cashInHandAndBank,
                    onValueChange = { onInputChange(state.zakatAssetInput.copy(cashInHandAndBank = it)) },
                    testTag = "input_cash_in_hand"
                )
                Spacer(Modifier.height(8.dp))
                NumericInputField(
                    label = "Foreign Currencies / Other Liquid Cash (${currency.symbol})",
                    value = state.zakatAssetInput.foreignCurrenciesInLocalVal,
                    onValueChange = { onInputChange(state.zakatAssetInput.copy(foreignCurrenciesInLocalVal = it)) },
                    testTag = "input_foreign_cash"
                )
            }
        }

        // Section 2: Gold Assets (Grams by Purity)
        item {
            AssetCategoryCard(
                title = "2. Gold Holdings (in Grams)",
                subtitle = "Bullion, coins, and personal/investment jewelry",
                icon = Icons.Default.Savings,
                totalValue = result.totalGoldValue,
                currency = currency
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumericInputField(
                        modifier = Modifier.weight(1f),
                        label = "24K Gold (g)",
                        value = state.zakatAssetInput.goldGrams24k,
                        onValueChange = { onInputChange(state.zakatAssetInput.copy(goldGrams24k = it)) },
                        testTag = "input_gold_24k"
                    )
                    NumericInputField(
                        modifier = Modifier.weight(1f),
                        label = "22K Gold (g)",
                        value = state.zakatAssetInput.goldGrams22k,
                        onValueChange = { onInputChange(state.zakatAssetInput.copy(goldGrams22k = it)) },
                        testTag = "input_gold_22k"
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumericInputField(
                        modifier = Modifier.weight(1f),
                        label = "21K Gold (g)",
                        value = state.zakatAssetInput.goldGrams21k,
                        onValueChange = { onInputChange(state.zakatAssetInput.copy(goldGrams21k = it)) },
                        testTag = "input_gold_21k"
                    )
                    NumericInputField(
                        modifier = Modifier.weight(1f),
                        label = "18K Gold (g)",
                        value = state.zakatAssetInput.goldGrams18k,
                        onValueChange = { onInputChange(state.zakatAssetInput.copy(goldGrams18k = it)) },
                        testTag = "input_gold_18k"
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "* Purity scaling applied automatically (22K = 91.6%, 21K = 87.5%, 18K = 75.0% pure)",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section 3: Silver Assets
        item {
            AssetCategoryCard(
                title = "3. Silver Holdings (in Grams)",
                subtitle = "Silver bars, coins, silverware & jewelry",
                icon = Icons.Default.Diamond,
                totalValue = result.totalSilverValue,
                currency = currency
            ) {
                NumericInputField(
                    label = "Total Pure Silver in Grams (g)",
                    value = state.zakatAssetInput.silverGrams,
                    onValueChange = { onInputChange(state.zakatAssetInput.copy(silverGrams = it)) },
                    testTag = "input_silver_grams"
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Current valuation: ${currency.symbol} ${formatNumber(result.totalSilverValue)}",
                    style = MaterialTheme.typography.labelSmall.copy(color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
                )
            }
        }

        // Section 4: Business Inventory & Investments
        item {
            AssetCategoryCard(
                title = "4. Business Inventory & Investments",
                subtitle = "'Urud al-Tijarah (merchandise for sale), stocks & funds",
                icon = Icons.Default.Storefront,
                totalValue = result.totalBusinessAndInvestments,
                currency = currency
            ) {
                NumericInputField(
                    label = "Business Merchandise & Stock-in-Trade (${currency.symbol})",
                    value = state.zakatAssetInput.businessInventoryGoods,
                    onValueChange = { onInputChange(state.zakatAssetInput.copy(businessInventoryGoods = it)) },
                    testTag = "input_business_inventory"
                )
                Spacer(Modifier.height(8.dp))
                NumericInputField(
                    label = "Shares, Mutual Funds & Crypto (${currency.symbol})",
                    value = state.zakatAssetInput.sharesAndMutualFunds + state.zakatAssetInput.cryptoAssets,
                    onValueChange = {
                        onInputChange(
                            state.zakatAssetInput.copy(
                                sharesAndMutualFunds = it,
                                cryptoAssets = 0.0
                            )
                        )
                    },
                    testTag = "input_shares_crypto"
                )
                Spacer(Modifier.height(8.dp))
                NumericInputField(
                    label = "Money Owed to You (Strong Receivables) (${currency.symbol})",
                    value = state.zakatAssetInput.moneyOwedToYouRecoverable,
                    onValueChange = { onInputChange(state.zakatAssetInput.copy(moneyOwedToYouRecoverable = it)) },
                    testTag = "input_receivables"
                )
            }
        }

        // Section 5: Deductible Immediate Liabilities
        item {
            AssetCategoryCard(
                title = "5. Immediate Liabilities (Deductible)",
                subtitle = "Debts due now & pending bills to be subtracted",
                icon = Icons.Default.MoneyOff,
                totalValue = result.totalLiabilities,
                currency = currency,
                isLiability = true
            ) {
                NumericInputField(
                    label = "Debts Due Immediately / Short-Term (${currency.symbol})",
                    value = state.zakatAssetInput.immediateDebtsDueNow,
                    onValueChange = { onInputChange(state.zakatAssetInput.copy(immediateDebtsDueNow = it)) },
                    testTag = "input_debts_due"
                )
                Spacer(Modifier.height(8.dp))
                NumericInputField(
                    label = "Due Living Expenses / Outstanding Bills (${currency.symbol})",
                    value = state.zakatAssetInput.currentMonthBillsAndExpenses,
                    onValueChange = { onInputChange(state.zakatAssetInput.copy(currentMonthBillsAndExpenses = it)) },
                    testTag = "input_bills_due"
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "* Per classical Fiqh, only debts due right now are deducted, not long-term amortized mortgages.",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ==========================================
// HERO ZAKAT RESULT CARD
// ==========================================
@Composable
private fun HeroZakatResultCard(
    result: ZakatCalculationResult,
    currency: CurrencyInfo,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_zakat_result_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            EmeraldPrimary.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL ZAKAT DUE (2.5%)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = EmeraldPrimary
                            )
                        )
                        Text(
                            text = "Rub' al-'Ushr • 1/40th of Net Zakatable Pool",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Status Badge (Eligible vs Below Nisab)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (result.isZakatPayable) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = if (result.isZakatPayable) "Zakat Obligatory (Fard)" else if (!result.isHawlMet) "Hawl Not Met" else "Below Nisab",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (result.isZakatPayable) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Big Zakat Amount
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = currency.symbol,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        ),
                        modifier = Modifier.padding(bottom = 4.dp, end = 4.dp)
                    )
                    Text(
                        text = formatNumber(result.zakatPayableAmount),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (result.isZakatPayable) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.testTag("zakat_payable_amount_text")
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = currency.code,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Breakdown metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Net Zakatable Wealth",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${currency.symbol} ${formatNumber(result.netZakatableWealth)}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Nisab Threshold (${result.selectedNisabStandard.label})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${currency.symbol} ${formatNumber(result.effectiveNisabThreshold)}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (result.isNetWealthAboveNisab) EmeraldPrimary else MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Actions: Save & Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_calculation_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Save Record", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_summary_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = EmeraldPrimary
                        )
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Copy Summary", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// NISAB & HAWL SETTINGS CARD
// ==========================================
@Composable
private fun NisabAndHawlSettingsCard(
    result: ZakatCalculationResult,
    currency: CurrencyInfo,
    selectedStandard: NisabStandard,
    onStandardChange: (NisabStandard) -> Unit,
    isHawlMet: Boolean,
    onHawlToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Nisab Standard & Hawl Condition",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Select your preferred Fiqh threshold based on Prophetic Hadith:",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))

            // Nisab Selector Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Silver Nisab
                FilterChip(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nisab_silver_chip"),
                    selected = selectedStandard == NisabStandard.SILVER,
                    onClick = { onStandardChange(NisabStandard.SILVER) },
                    label = {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("Silver (595g)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                text = "${currency.symbol} ${formatNumber(result.nisabThresholdSilver)}",
                                fontSize = 11.sp,
                                color = if (selectedStandard == NisabStandard.SILVER) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Diamond,
                            contentDescription = null,
                            tint = if (selectedStandard == NisabStandard.SILVER) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )

                // Gold Nisab
                FilterChip(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nisab_gold_chip"),
                    selected = selectedStandard == NisabStandard.GOLD,
                    onClick = { onStandardChange(NisabStandard.GOLD) },
                    label = {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("Gold (85g)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                text = "${currency.symbol} ${formatNumber(result.nisabThresholdGold)}",
                                fontSize = 11.sp,
                                color = if (selectedStandard == NisabStandard.GOLD) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Savings,
                            contentDescription = null,
                            tint = if (selectedStandard == NisabStandard.GOLD) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = if (selectedStandard == NisabStandard.SILVER)
                    "💡 Silver standard is preferred by the classical majority (Hanafi consensus) as it ensures the widest benefit to the poor (Anfa' li al-Fuqara)."
                else
                    "💡 Gold standard (20 Dinars / 85g 24K) is adopted by some contemporary councils for sole cash wealth.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            // Hawl (1 Lunar Year) Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hawl (1 Full Lunar Year) Passed",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = EmeraldPrimary
                        )
                    }
                    Text(
                        text = "\"There is no Zakat upon wealth until a full year passes over it.\" (Sunan Ibn Majah #1792)",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = isHawlMet,
                    onCheckedChange = { onHawlToggle(it) },
                    modifier = Modifier.testTag("zakat_hawl_switch"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = EmeraldPrimary,
                        checkedTrackColor = EmeraldPrimary.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}

// ==========================================
// ASSET CATEGORY CARD COMPONENT
// ==========================================
@Composable
private fun AssetCategoryCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    totalValue: Double,
    currency: CurrencyInfo,
    isLiability: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (isLiability) MaterialTheme.colorScheme.error.copy(alpha = 0.12f) else EmeraldPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isLiability) MaterialTheme.colorScheme.error else EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Category Total Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isLiability) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else EmeraldPrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "${if (isLiability) "- " else ""}${currency.symbol} ${formatNumber(totalValue)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isLiability) MaterialTheme.colorScheme.error else EmeraldPrimary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

// ==========================================
// NUMERIC INPUT FIELD
// ==========================================
@Composable
private fun NumericInputField(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    var textValue by remember(value) {
        mutableStateOf(if (value == 0.0) "" else if (value % 1.0 == 0.0) value.toLong().toString() else value.toString())
    }

    OutlinedTextField(
        value = textValue,
        onValueChange = { input ->
            val cleaned = input.filter { it.isDigit() || it == '.' }
            textValue = cleaned
            val parsed = cleaned.toDoubleOrNull() ?: 0.0
            onValueChange(parsed)
        },
        label = { Text(label, fontSize = 12.sp) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EmeraldPrimary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

// ==========================================
// 3. HADITH PROOFS TAB CONTENT
// ==========================================
@Composable
private fun HadithProofsTabContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EmeraldPrimary.copy(alpha = 0.08f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Prophetic Sunnah as the Lawgiver's Standard",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                        Text(
                            text = "The rates, Nisab thresholds, and Hawl period are definitively established by authentic Ahadith.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Hadith 1: Gold Nisab
        item {
            HadithProofItem(
                title = "1. Nisab of Gold (20 Mithqals / Dinars = 85 Grams)",
                narrator = "Narrated by 'Ali ibn Abi Talib (RA)",
                source = "Sunan Abi Dawud #1573 • Sahih Albani",
                arabic = "لَيْسَ عَلَيْكَ شَىْءٌ يَعْنِي فِي الذَّهَبِ حَتَّى يَكُونَ لَكَ عِشْرُونَ دِينَارًا فَإِذَا كَانَتْ لَكَ عِشْرُونَ دِينَارًا وَحَالَ عَلَيْهَا الْحَوْلُ فَفِيهَا نِصْفُ دِينَارٍ",
                translation = "The Prophet (ﷺ) said: 'You are not obligated to pay anything in gold until you have twenty dinars (mithqals). When you possess twenty dinars and a year (hawl) passes over them, there is due on them half a dinar (2.5%).'",
                fiqhNote = "Scholarly consensus across the 4 Madhabs calculates 1 Mithqal/Dinar as 4.25 grams. Therefore 20 Dinars = 85.0 grams of 24K gold."
            )
        }

        // Hadith 2: Silver Nisab
        item {
            HadithProofItem(
                title = "2. Nisab of Silver (200 Dirhams / 5 Awaq = 595 Grams)",
                narrator = "Narrated by Abu Sa'id al-Khudri (RA)",
                source = "Sahih al-Bukhari #1405 • Sahih Muslim #979",
                arabic = "لَيْسَ فِيمَا دُونَ خَمْسِ أَوَاقٍ مِنَ الْوَرِقِ صَدَقَةٌ",
                translation = "The Messenger of Allah (ﷺ) said: 'No Zakat is due on silver below five awaq (equivalent to 200 silver Dirhams).'",
                fiqhNote = "1 silver Dirham is 2.975 grams. 200 Dirhams equals 595.0 grams of pure silver. This constitutes the Nisab for silver."
            )
        }

        // Hadith 3: The 2.5% Rate (Rub' al-'Ushr)
        item {
            HadithProofItem(
                title = "3. The Mandatory Rate: 2.5% (Rub' al-'Ushr / 1/40th)",
                narrator = "Narrated by Anas ibn Malik (RA) from Abu Bakr's decree",
                source = "Sahih al-Bukhari #1454 (Kitab az-Zakat)",
                arabic = "وَفِي الرِّقَةِ رُبْعُ الْعُشْرِ فَإِنْ لَمْ تَكُنْ إِلاَّ تِسْعِينَ وَمِائَةً فَلَيْسَ فِيهَا شَىْءٌ إِلاَّ أَنْ يَشَاءَ رَبُّهَا",
                translation = "In the official instructions written by Abu Bakr (RA) as commanded by Allah's Messenger (ﷺ): 'And on silver coin (and cash wealth), one-fourth of a tenth (2.5%) is due.'",
                fiqhNote = "Rub' al-'Ushr (2.5%) applies uniformly to all monetary wealth, gold, silver, and commercial inventory ('Urud al-Tijarah)."
            )
        }

        // Hadith 4: The Hawl Condition
        item {
            HadithProofItem(
                title = "4. The Hawl Condition (One Full Lunar Year)",
                narrator = "Narrated by Ibn 'Umar (RA) and 'Aisha (RA)",
                source = "Sunan Ibn Majah #1792 • Sunan at-Tirmidhi #631",
                arabic = "لاَ زَكَاةَ فِي مَالٍ حَتَّى يَحُولَ عَلَيْهِ الْحَوْلُ",
                translation = "The Messenger of Allah (ﷺ) said: 'There is no Zakat upon wealth until a full lunar year (Hawl) has passed over it.'",
                fiqhNote = "Wealth must remain above Nisab for a complete lunar year (approximately 354 days) to ensure stability before Zakat becomes due."
            )
        }

        // Madhab Jurisprudential Matrix
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Madhab Perspectives on Personal Jewelry",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "• Hanafi School: Zakat is obligatory on all gold and silver, whether held for investment, trade, or personal permissible jewelry, if it reaches Nisab.\n" +
                                "• Shafi'i, Maliki, Hanbali Schools: Permissible personal jewelry worn within customary limits without excess is exempt from Zakat; Zakat is strictly due on bullion, coins, savings, and trade assets.\n" +
                                "• Precautionary View (Al-Ahwat): Many contemporary scholars advise calculating Zakat on all jewelry to purify one's wealth without doubt.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 17.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun HadithProofItem(
    title: String,
    narrator: String,
    source: String,
    arabic: String,
    translation: String,
    fiqhNote: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = EmeraldPrimary
            )
            Text(
                text = "$narrator • $source",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EmeraldPrimary.copy(alpha = 0.05f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = arabic,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Right,
                        lineHeight = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = translation,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Fiqh Application: $fiqhNote",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, lineHeight = 15.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

// ==========================================
// 4. 8 BENEFICIARIES TAB CONTENT
// ==========================================
@Composable
private fun BeneficiariesTabContent() {
    val beneficiaries = remember { ZakatBeneficiary.MASARIF_LIST }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Surah At-Tawbah Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EmeraldPrimary.copy(alpha = 0.08f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Masarif al-Zakat (The 8 Quranic Categories)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldPrimary
                    )
                    Text(
                        text = "Surah At-Tawbah (9:60)",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "إِنَّمَا الصَّدَقَاتُ لِلْفُقَرَاءِ وَالْمَسَاكِينِ وَالْعَامِلِينَ عَلَيْهَا وَالْمُؤَلَّفَةِ قُلُوبُهُمْ وَفِي الرِّقَابِ وَالْغَارِمِينَ وَفِي سَبِيلِ اللَّهِ وَابْنِ السَّبِيلِ ۖ فَرِيضَةً مِّنَ اللَّهِ ۗ وَاللَّهُ عَلِيمٌ حَكِيمٌ",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Right,
                            lineHeight = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "\"Zakat expenditures are only for the poor and for the needy and for those employed to collect [zakat] and for bringing hearts together [for Islam] and for freeing captives [or slaves] and for those in debt and for the cause of Allah and for the [stranded] traveler - an obligation [imposed] by Allah. And Allah is Knowing and Wise.\"",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Beneficiary Cards
        items(beneficiaries) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        shape = CircleShape,
                        color = EmeraldPrimary,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = item.number.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = item.arabicName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Ineligible Recipients Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Who Cannot Receive Zakat?",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "1. Direct Ascendants & Descendants: Parents, grandparents, children, and grandchildren (as one is already legally obligated to support them).\n" +
                                "2. Spouse: A husband cannot give Zakat to his wife.\n" +
                                "3. The Wealthy: Anyone who possesses wealth equal to or exceeding Nisab.\n" +
                                "4. The Household of the Prophet (Banu Hashim): Barred from taking Zakat per authentic Hadith.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// ==========================================
// 5. SAVED HISTORY TAB CONTENT
// ==========================================
@Composable
private fun SavedHistoryTabContent(
    records: List<SavedZakatRecord>,
    currency: CurrencyInfo,
    onDelete: (String) -> Unit
) {
    if (records.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "No Saved Calculations Yet",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Calculate your annual Zakat and tap 'Save Record' to track your historical payments across lunar years.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(records) { record ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = record.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = record.formattedDate,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = { onDelete(record.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete Record",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Net Wealth",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${record.currencyCode} ${formatNumber(record.netWealth)}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Column {
                                Text(
                                    text = "Nisab Standard",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = record.nisabStandard,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Zakat Paid",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldPrimary
                                )
                                Text(
                                    text = "${record.currencyCode} ${formatNumber(record.zakatAmount)}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    )
                                )
                            }
                        }

                        if (record.notes.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Note: ${record.notes}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. CURRENCY PICKER MODAL
// ==========================================
@Composable
private fun CurrencyPickerModal(
    currentCurrency: CurrencyInfo,
    onSelectCurrency: (CurrencyInfo) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredList = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            CurrencyInfo.ALL_CURRENCIES
        } else {
            CurrencyInfo.ALL_CURRENCIES.filter {
                it.code.contains(searchQuery, ignoreCase = true) ||
                        it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.75f)
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Currency",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Values, live precious metal rates, and Nisab will automatically convert to the selected currency.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search currency by code or country...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("currency_search_input"),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredList) { currency ->
                        val isSelected = currency.code == currentCurrency.code
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onSelectCurrency(currency) }
                                .testTag("currency_item_${currency.code}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = currency.flagEmoji, fontSize = 20.sp)
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "${currency.code} - ${currency.name}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Symbol: ${currency.symbol} • Exchange: ${currency.usdExchangeRate} / USD",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. SAVE ZAKAT DIALOG
// ==========================================
@Composable
private fun SaveZakatRecordDialog(
    result: ZakatCalculationResult,
    currency: CurrencyInfo,
    onConfirmSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val currentYear = remember { SimpleDateFormat("yyyy", Locale.getDefault()).format(Date()) }
    var title by remember { mutableStateOf("Annual Zakat $currentYear") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Zakat Record", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Archive this calculation to track your lunar year Zakat contributions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Zakat Due: ${currency.symbol} ${formatNumber(result.zakatPayableAmount)} ${currency.code}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        )
                        Text(
                            text = "Net Zakatable Wealth: ${currency.symbol} ${formatNumber(result.netZakatableWealth)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp)
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Record Title") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_zakat_title_input")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (e.g. distributed to local families)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_zakat_notes_input"),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmSave(title, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("confirm_save_zakat_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ==========================================
// 8. CUSTOM PRECIOUS METALS DIALOG
// ==========================================
@Composable
private fun CustomPreciousMetalsDialog(
    currentRates: GoldSilverRates,
    currency: CurrencyInfo,
    onSaveCustom: (Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var goldRateText by remember { mutableStateOf(currentRates.goldPricePerGramUSD.toString()) }
    var silverRateText by remember { mutableStateOf(currentRates.silverPricePerGramUSD.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Spot Price Adjuster", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "If your local bullion market or jeweller charges a different spot rate, you can adjust the base benchmark price per gram here:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = goldRateText,
                    onValueChange = { goldRateText = it },
                    label = { Text("24K Gold Price / gram (USD)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = silverRateText,
                    onValueChange = { silverRateText = it },
                    label = { Text("Pure Silver Price / gram (USD)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val g = goldRateText.toDoubleOrNull() ?: currentRates.goldPricePerGramUSD
                    val s = silverRateText.toDoubleOrNull() ?: currentRates.silverPricePerGramUSD
                    onSaveCustom(g, s)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ==========================================
// UTILITY HELPERS
// ==========================================
private fun formatNumber(value: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
    formatter.maximumFractionDigits = 2
    formatter.minimumFractionDigits = 0
    return formatter.format(value)
}

private fun formatZakatSummary(result: ZakatCalculationResult, currency: CurrencyInfo): String {
    return """
        🕌 Deen365 Zakat Calculation Summary
        ════════════════════════════════════
        Currency: ${currency.name} (${currency.code})
        Gross Assets: ${currency.symbol} ${formatNumber(result.grossAssets)}
        Total Deductible Liabilities: ${currency.symbol} ${formatNumber(result.totalLiabilities)}
        Net Zakatable Wealth: ${currency.symbol} ${formatNumber(result.netZakatableWealth)}
        
        Nisab Threshold (${result.selectedNisabStandard.label}): ${currency.symbol} ${formatNumber(result.effectiveNisabThreshold)}
        Eligible for Zakat: ${if (result.isNetWealthAboveNisab) "YES (Above Nisab)" else "NO (Below Nisab)"}
        Hawl (1 Lunar Year) Met: ${if (result.isHawlMet) "YES" else "NO"}
        
        Rate: 2.5% (Rub' al-'Ushr - Sunan Abi Dawud #1573)
        TOTAL ZAKAT DUE: ${currency.symbol} ${formatNumber(result.zakatPayableAmount)} ${currency.code}
        ════════════════════════════════════
        Calculated according to the Prophetic Sunnah on Deen365.
    """.trimIndent()
}
