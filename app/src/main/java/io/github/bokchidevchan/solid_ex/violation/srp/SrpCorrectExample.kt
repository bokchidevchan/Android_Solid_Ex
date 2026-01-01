package io.github.bokchidevchan.solid_ex.violation.srp

import io.github.bokchidevchan.solid_ex.domain.model.Payment
import io.github.bokchidevchan.solid_ex.domain.model.PaymentType
import io.github.bokchidevchan.solid_ex.domain.repository.PaymentRepository
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * ✅ SRP 준수 예제
 *
 * 각 클래스가 단 하나의 책임만 가진다.
 * 변경 이유가 하나뿐이므로 유지보수가 쉽다.
 */

// ========================================
// 1. 수수료 계산 - 수수료 정책 변경 시에만 수정
// ========================================
class FeeCalculator @Inject constructor() {

    // 수수료 정책이 변경되면 이 클래스만 수정하면 된다
    fun calculate(payment: Payment): Int {
        return when (payment.type) {
            PaymentType.CARD -> (payment.amount * CARD_FEE_RATE).toInt()
            PaymentType.BANK -> BANK_FEE_FIXED
            PaymentType.CASH -> 0
            PaymentType.GIFT -> (payment.amount * GIFT_FEE_RATE).toInt()
        }
    }

    companion object {
        private const val CARD_FEE_RATE = 0.03
        private const val GIFT_FEE_RATE = 0.05
        private const val BANK_FEE_FIXED = 500
    }
}

// ========================================
// 2. 포맷팅 - 표시 형식 변경 시에만 수정
// ========================================
class PaymentFormatter @Inject constructor() {

    private val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
    private val dateFormat = SimpleDateFormat("MM월 dd일 HH:mm", Locale.KOREA)

    // 다국어 지원이 필요하면 이 클래스만 수정하면 된다
    fun formatAmount(amount: Int): String {
        return numberFormat.format(amount) + "원"
    }

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatPercentage(amount: Int, total: Int): String {
        if (total == 0) return "0%"
        val percentage = (amount.toFloat() / total * 100)
        return String.format("%.1f%%", percentage)
    }

    fun getTypeLabel(type: PaymentType): String {
        return type.label
    }
}

// ========================================
// 3. 통계 계산 - 통계 로직 변경 시에만 수정
// ========================================
class StatisticsCalculator @Inject constructor() {

    fun calculate(payments: List<Payment>): PaymentStatistics {
        val totalAmount = payments.sumOf { it.amount }
        val totalFee = payments.sumOf { it.fee }
        val averageAmount = if (payments.isNotEmpty()) totalAmount / payments.size else 0
        val count = payments.size

        return PaymentStatistics(
            totalAmount = totalAmount,
            totalFee = totalFee,
            averageAmount = averageAmount,
            count = count
        )
    }
}

data class PaymentStatistics(
    val totalAmount: Int,
    val totalFee: Int,
    val averageAmount: Int,
    val count: Int
)

// ========================================
// 4. 필터링 - 필터 조건 변경 시에만 수정
// ========================================
class FilterPaymentsUseCase @Inject constructor() {

    operator fun invoke(
        payments: List<Payment>,
        type: PaymentType?
    ): List<Payment> {
        return type?.let { filterType ->
            payments.filter { it.type == filterType }
        } ?: payments
    }
}

// ========================================
// 5. 정렬 - 정렬 기준 변경 시에만 수정
// ========================================
class SortPaymentsUseCase @Inject constructor() {

    operator fun invoke(
        payments: List<Payment>,
        sortOrder: SortOrder = SortOrder.DATE_DESC
    ): List<Payment> {
        return when (sortOrder) {
            SortOrder.DATE_DESC -> payments.sortedByDescending { it.timestamp }
            SortOrder.DATE_ASC -> payments.sortedBy { it.timestamp }
            SortOrder.AMOUNT_DESC -> payments.sortedByDescending { it.amount }
            SortOrder.AMOUNT_ASC -> payments.sortedBy { it.amount }
        }
    }
}

enum class SortOrder {
    DATE_DESC, DATE_ASC, AMOUNT_DESC, AMOUNT_ASC
}

// ========================================
// 6. 수수료 적용 - 수수료 적용 로직 변경 시에만 수정
// ========================================
class ApplyFeesUseCase @Inject constructor(
    private val feeCalculator: FeeCalculator
) {
    operator fun invoke(payments: List<Payment>): List<Payment> {
        return payments.map { payment ->
            payment.copy(fee = feeCalculator.calculate(payment))
        }
    }
}

// ========================================
// 7. 결제 목록 조회 (조합) - 흐름 변경 시에만 수정
// ========================================
class GetPaymentsUseCase @Inject constructor(
    private val repository: PaymentRepository,
    private val filterUseCase: FilterPaymentsUseCase,
    private val sortUseCase: SortPaymentsUseCase,
    private val applyFeesUseCase: ApplyFeesUseCase
) {
    suspend operator fun invoke(
        filterType: PaymentType? = null,
        sortOrder: SortOrder = SortOrder.DATE_DESC
    ): List<Payment> {
        val payments = repository.getPayments()
        val filtered = filterUseCase(payments, filterType)
        val sorted = sortUseCase(filtered, sortOrder)
        val withFees = applyFeesUseCase(sorted)
        return withFees
    }
}
