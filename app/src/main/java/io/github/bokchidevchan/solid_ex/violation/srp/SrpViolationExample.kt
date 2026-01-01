package io.github.bokchidevchan.solid_ex.violation.srp

import androidx.compose.foundation.clickable
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
import io.github.bokchidevchan.solid_ex.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * ❌ SRP (Single Responsibility Principle) 위반 예제
 *
 * SRP 원칙: 클래스는 단 하나의 이유로만 변경되어야 한다.
 *
 * 이 ViewModel은 너무 많은 책임을 가지고 있습니다:
 * 1. 결제 데이터 로딩 (데이터 계층 책임)
 * 2. 필터링 로직 (비즈니스 로직)
 * 3. 정렬 로직 (비즈니스 로직)
 * 4. 수수료 계산 (비즈니스 로직)
 * 5. 금액 포맷팅 (프레젠테이션 로직)
 * 6. 날짜 포맷팅 (프레젠테이션 로직)
 * 7. 타입 라벨 변환 (프레젠테이션 로직)
 * 8. 통계 계산 (비즈니스 로직)
 *
 * 문제점:
 * - 포맷팅 로직이 변경되면 ViewModel을 수정해야 함
 * - 수수료 정책이 변경되면 ViewModel을 수정해야 함
 * - 정렬 정책이 변경되면 ViewModel을 수정해야 함
 * - 테스트하기 어려움 (모든 로직이 한 곳에)
 */
@HiltViewModel
class SrpViolationViewModel @Inject constructor(
    private val repository: PaymentRepository  // DIP는 지켜짐 (인터페이스 의존)
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Loading)
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

    private fun loadPayments() {
        viewModelScope.launch {
            _uiState.value = PaymentUiState.Loading

            try {
                val payments = repository.getPayments()

                // ❌ SRP 위반: 필터링 로직이 ViewModel에 있음
                // 이 로직은 별도의 FilterUseCase로 분리되어야 함
                val filtered = _selectedFilter.value?.let { type ->
                    payments.filter { it.type == type }
                } ?: payments

                // ❌ SRP 위반: 정렬 로직이 ViewModel에 있음
                // 이 로직은 별도의 SortUseCase로 분리되어야 함
                val sorted = filtered.sortedByDescending { it.timestamp }

                // ❌ SRP 위반: 수수료 계산 로직이 ViewModel에 있음
                // 이 로직은 별도의 FeeCalculator로 분리되어야 함
                val withFee = sorted.map { payment ->
                    val fee = calculateFee(payment)
                    payment.copy(fee = fee)
                }

                // ❌ SRP 위반: 통계 계산 로직이 ViewModel에 있음
                // 이 로직은 별도의 StatisticsCalculator로 분리되어야 함
                val totalAmount = withFee.sumOf { it.amount }
                val totalFee = withFee.sumOf { it.fee }
                val averageAmount = if (withFee.isNotEmpty()) totalAmount / withFee.size else 0

                _uiState.value = PaymentUiState.Success(
                    payments = withFee,
                    totalAmount = totalAmount,
                    totalFee = totalFee,
                    averageAmount = averageAmount
                )

            } catch (e: Exception) {
                _uiState.value = PaymentUiState.Error("데이터를 불러오는데 실패했습니다")
            }
        }
    }

    // ❌ SRP 위반: 수수료 계산 로직
    // 이것은 별도의 FeeCalculator 클래스로 분리되어야 함
    private fun calculateFee(payment: Payment): Int {
        return when (payment.type) {
            PaymentType.CARD -> (payment.amount * 0.03).toInt()
            PaymentType.BANK -> 500
            PaymentType.CASH -> 0
            PaymentType.GIFT -> (payment.amount * 0.05).toInt()
        }
    }

    // ❌ SRP 위반: 포맷팅 로직들
    // 이것은 별도의 PaymentFormatter 클래스로 분리되어야 함
    fun formatAmount(amount: Int): String {
        return NumberFormat.getNumberInstance(Locale.KOREA).format(amount) + "원"
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MM월 dd일 HH:mm", Locale.KOREA)
        return sdf.format(Date(timestamp))
    }

    fun getTypeLabel(type: PaymentType): String {
        return type.label
    }

    // ❌ SRP 위반: 퍼센트 계산 로직
    fun calculatePercentage(amount: Int, total: Int): String {
        if (total == 0) return "0%"
        val percentage = (amount.toFloat() / total * 100)
        return String.format("%.1f%%", percentage)
    }
}

sealed class PaymentUiState {
    data object Loading : PaymentUiState()
    data class Success(
        val payments: List<Payment>,
        val totalAmount: Int,
        val totalFee: Int,
        val averageAmount: Int
    ) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
}

// ========================================
// UI (Composable)
// ========================================

@Composable
fun SrpViolationScreen(
    viewModel: SrpViolationViewModel,
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
            text = "SRP 위반 예제",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "ViewModel이 너무 많은 책임을 가짐",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        FilterChipRow(
            selectedFilter = selectedFilter,
            onFilterSelected = { viewModel.selectFilter(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is PaymentUiState.Loading -> LoadingContent()
            is PaymentUiState.Success -> SuccessContent(
                state = state,
                viewModel = viewModel
            )
            is PaymentUiState.Error -> ErrorContent(message = state.message)
        }
    }
}

@Composable
private fun FilterChipRow(
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
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun SuccessContent(
    state: PaymentUiState.Success,
    viewModel: SrpViolationViewModel
) {
    Column {
        // 통계 카드
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
                        viewModel.formatAmount(state.totalAmount),
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
                    Text(viewModel.formatAmount(state.totalFee))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("평균 결제", style = MaterialTheme.typography.bodyMedium)
                    Text(viewModel.formatAmount(state.averageAmount))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.payments, key = { it.id }) { payment ->
                PaymentItem(
                    payment = payment,
                    formattedAmount = viewModel.formatAmount(payment.amount),
                    formattedFee = if (payment.fee > 0) "(수수료 ${viewModel.formatAmount(payment.fee)})" else "",
                    formattedDate = viewModel.formatDate(payment.timestamp),
                    typeLabel = viewModel.getTypeLabel(payment.type)
                )
            }
        }
    }
}

@Composable
private fun PaymentItem(
    payment: Payment,
    formattedAmount: String,
    formattedFee: String,
    formattedDate: String,
    typeLabel: String
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
                    Text(typeLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(formattedDate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formattedAmount, fontWeight = FontWeight.Bold)
                if (formattedFee.isNotEmpty()) {
                    Text(formattedFee, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}
