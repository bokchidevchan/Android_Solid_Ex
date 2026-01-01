package io.github.bokchidevchan.solid_ex.violation.srp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.bokchidevchan.solid_ex.domain.model.Payment
import io.github.bokchidevchan.solid_ex.domain.model.PaymentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ✅ SRP 준수 ViewModel
 *
 * 이 ViewModel은 오직 UI 상태 관리만 담당한다.
 * - 데이터 로딩 → GetPaymentsUseCase
 * - 통계 계산 → StatisticsCalculator
 * - 포맷팅 → PaymentFormatter
 *
 * 변경 이유: UI 상태 관리 방식이 변경될 때만
 */
@HiltViewModel
class SrpCorrectViewModel @Inject constructor(
    private val getPaymentsUseCase: GetPaymentsUseCase,
    private val statisticsCalculator: StatisticsCalculator,
    val formatter: PaymentFormatter  // UI에서 사용하므로 public
) : ViewModel() {

    private val _uiState = MutableStateFlow<SrpCorrectUiState>(SrpCorrectUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _selectedFilter = MutableStateFlow<PaymentType?>(null)
    val selectedFilter = _selectedFilter.asStateFlow()

    init {
        loadPayments()
    }

    fun selectFilter(type: PaymentType?) {
        _selectedFilter.value = type
        loadPayments()
    }

    // ViewModel은 UseCase를 조합하고 UI 상태만 관리한다
    private fun loadPayments() {
        viewModelScope.launch {
            _uiState.value = SrpCorrectUiState.Loading

            try {
                // 데이터 가져오기 (필터링, 정렬, 수수료 적용은 UseCase가 처리)
                val payments = getPaymentsUseCase(
                    filterType = _selectedFilter.value
                )

                // 통계 계산은 StatisticsCalculator가 처리
                val statistics = statisticsCalculator.calculate(payments)

                _uiState.value = SrpCorrectUiState.Success(
                    payments = payments,
                    statistics = statistics
                )

            } catch (e: Exception) {
                _uiState.value = SrpCorrectUiState.Error("데이터를 불러오는데 실패했습니다")
            }
        }
    }
}

sealed class SrpCorrectUiState {
    data object Loading : SrpCorrectUiState()
    data class Success(
        val payments: List<Payment>,
        val statistics: PaymentStatistics
    ) : SrpCorrectUiState()
    data class Error(val message: String) : SrpCorrectUiState()
}

// ========================================
// UI (Composable) - 포맷팅은 Formatter에 위임
// ========================================

@Composable
fun SrpCorrectScreen(
    viewModel: SrpCorrectViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "SRP 준수 예제",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "각 클래스가 단일 책임만 가짐",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        CorrectFilterChipRow(
            selectedFilter = selectedFilter,
            onFilterSelected = { viewModel.selectFilter(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is SrpCorrectUiState.Loading -> CorrectLoadingContent()
            is SrpCorrectUiState.Success -> CorrectSuccessContent(
                state = state,
                formatter = viewModel.formatter
            )
            is SrpCorrectUiState.Error -> CorrectErrorContent(message = state.message)
        }
    }
}

@Composable
private fun CorrectFilterChipRow(
    selectedFilter: PaymentType?,
    onFilterSelected: (PaymentType?) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            FilterChip(
                selected = selectedFilter == null,
                onClick = { onFilterSelected(null) },
                label = { Text("전체") }
            )
        }
        items(PaymentType.entries.toList()) { type ->
            FilterChip(
                selected = selectedFilter == type,
                onClick = { onFilterSelected(type) },
                label = { Text(type.label) }
            )
        }
    }
}

@Composable
private fun CorrectLoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CorrectSuccessContent(
    state: SrpCorrectUiState.Success,
    formatter: PaymentFormatter  // 포맷팅은 Formatter에게 위임
) {
    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("총 지출", style = MaterialTheme.typography.titleMedium)
                    Text(
                        formatter.formatAmount(state.statistics.totalAmount),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("총 수수료", style = MaterialTheme.typography.bodyMedium)
                    Text(formatter.formatAmount(state.statistics.totalFee))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("평균 결제", style = MaterialTheme.typography.bodyMedium)
                    Text(formatter.formatAmount(state.statistics.averageAmount))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.payments, key = { it.id }) { payment ->
                CorrectPaymentItem(
                    payment = payment,
                    formatter = formatter
                )
            }
        }
    }
}

@Composable
private fun CorrectPaymentItem(
    payment: Payment,
    formatter: PaymentFormatter
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(payment.title, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        formatter.getTypeLabel(payment.type),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        formatter.formatDate(payment.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatter.formatAmount(payment.amount), fontWeight = FontWeight.Bold)
                if (payment.fee > 0) {
                    Text(
                        "(수수료 ${formatter.formatAmount(payment.fee)})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun CorrectErrorContent(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}
