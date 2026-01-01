package io.github.bokchidevchan.solid_ex.violation.ocp

import io.github.bokchidevchan.solid_ex.domain.model.Payment
import io.github.bokchidevchan.solid_ex.domain.model.PaymentType
import javax.inject.Inject

/**
 * ❌ OCP (Open-Closed Principle) 위반 예제
 *
 * OCP 원칙: 소프트웨어 엔티티는 확장에는 열려있고, 수정에는 닫혀있어야 한다.
 *
 * 이 클래스들은 새로운 결제 타입이 추가될 때마다 기존 코드를 수정해야 합니다.
 *
 * 문제점:
 * - 새 결제 타입(예: POINT, CRYPTO) 추가 시 모든 when 분기를 수정해야 함
 * - 수정 시 기존에 잘 동작하던 코드에 버그가 발생할 수 있음
 * - 수정해야 할 위치를 찾기 어려움 (여러 곳에 분산됨)
 */

/**
 * ❌ OCP 위반: 수수료 계산기
 * 새 결제 타입 추가 시 이 when 분기를 수정해야 함
 */
class FeeCalculator @Inject constructor() {

    fun calculate(payment: Payment): Int {
        // ❌ OCP 위반: 새 타입 추가 시 여기를 수정해야 함
        return when (payment.type) {
            PaymentType.CARD -> calculateCardFee(payment.amount)
            PaymentType.BANK -> calculateBankFee(payment.amount)
            PaymentType.CASH -> calculateCashFee(payment.amount)
            PaymentType.GIFT -> calculateGiftFee(payment.amount)
            // 새 타입이 추가되면?
            // PaymentType.POINT -> ???
            // PaymentType.CRYPTO -> ???
            // 이 파일을 수정해야 함!
        }
    }

    private fun calculateCardFee(amount: Int): Int = (amount * 0.03).toInt()
    private fun calculateBankFee(amount: Int): Int = 500
    private fun calculateCashFee(amount: Int): Int = 0
    private fun calculateGiftFee(amount: Int): Int = (amount * 0.05).toInt()
}

/**
 * ❌ OCP 위반: 결제 검증기
 * 새 결제 타입 추가 시 이 when 분기도 수정해야 함
 */
class PaymentValidator @Inject constructor() {

    fun validate(payment: Payment): ValidationResult {
        // ❌ OCP 위반: 새 타입 추가 시 여기를 수정해야 함
        return when (payment.type) {
            PaymentType.CARD -> validateCard(payment)
            PaymentType.BANK -> validateBank(payment)
            PaymentType.CASH -> validateCash(payment)
            PaymentType.GIFT -> validateGift(payment)
            // 새 타입이 추가되면 여기도 수정!
        }
    }

    private fun validateCard(payment: Payment): ValidationResult {
        return if (payment.amount <= 5000000) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("카드 결제는 500만원 이하만 가능합니다")
        }
    }

    private fun validateBank(payment: Payment): ValidationResult {
        return if (payment.amount <= 10000000) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("계좌이체는 1000만원 이하만 가능합니다")
        }
    }

    private fun validateCash(payment: Payment): ValidationResult {
        return if (payment.amount <= 1000000) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("현금 결제는 100만원 이하만 가능합니다")
        }
    }

    private fun validateGift(payment: Payment): ValidationResult {
        return if (payment.amount <= 500000) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("상품권은 50만원 이하만 가능합니다")
        }
    }
}

sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}

/**
 * ❌ OCP 위반: 결제 아이콘 제공자
 * 새 결제 타입 추가 시 이 when 분기도 수정해야 함
 */
class PaymentIconProvider @Inject constructor() {

    fun getIcon(type: PaymentType): String {
        // ❌ OCP 위반: 새 타입 추가 시 여기를 수정해야 함
        return when (type) {
            PaymentType.CARD -> "💳"
            PaymentType.BANK -> "🏦"
            PaymentType.CASH -> "💵"
            PaymentType.GIFT -> "🎁"
            // 새 타입이 추가되면 여기도 수정!
        }
    }

    fun getColor(type: PaymentType): Long {
        // ❌ OCP 위반: 새 타입 추가 시 여기를 수정해야 함
        return when (type) {
            PaymentType.CARD -> 0xFF1976D2  // Blue
            PaymentType.BANK -> 0xFF388E3C  // Green
            PaymentType.CASH -> 0xFFF57C00  // Orange
            PaymentType.GIFT -> 0xFFE91E63  // Pink
            // 새 타입이 추가되면 여기도 수정!
        }
    }
}

/**
 * ❌ OCP 위반: 결제 리포트 생성기
 * 새 결제 타입 추가 시 이 when 분기도 수정해야 함
 */
class PaymentReportGenerator @Inject constructor() {

    fun generateReport(payments: List<Payment>): String {
        val sb = StringBuilder()
        sb.appendLine("=== 결제 리포트 ===")
        sb.appendLine()

        // ❌ OCP 위반: 새 타입 추가 시 여기를 수정해야 함
        PaymentType.entries.forEach { type ->
            val typePayments = payments.filter { it.type == type }
            val total = typePayments.sumOf { it.amount }

            val typeName = when (type) {
                PaymentType.CARD -> "카드 결제"
                PaymentType.BANK -> "계좌이체"
                PaymentType.CASH -> "현금 결제"
                PaymentType.GIFT -> "상품권"
                // 새 타입이 추가되면 여기도 수정!
            }

            sb.appendLine("$typeName: ${typePayments.size}건, 총 $total 원")
        }

        return sb.toString()
    }

    fun getExportFormat(type: PaymentType): String {
        // ❌ OCP 위반: 새 타입 추가 시 여기를 수정해야 함
        return when (type) {
            PaymentType.CARD -> "CSV"
            PaymentType.BANK -> "PDF"
            PaymentType.CASH -> "TXT"
            PaymentType.GIFT -> "JSON"
            // 새 타입이 추가되면 여기도 수정!
        }
    }
}

/**
 * ❌ OCP 위반의 문제점 시뮬레이션
 *
 * 만약 새로운 결제 타입 "POINT"를 추가한다면:
 *
 * 1. PaymentType enum에 POINT 추가
 * 2. FeeCalculator.calculate()의 when 분기 수정
 * 3. PaymentValidator.validate()의 when 분기 수정
 * 4. PaymentIconProvider.getIcon()의 when 분기 수정
 * 5. PaymentIconProvider.getColor()의 when 분기 수정
 * 6. PaymentReportGenerator.generateReport()의 when 분기 수정
 * 7. PaymentReportGenerator.getExportFormat()의 when 분기 수정
 * 8. ... 그 외 결제 타입을 다루는 모든 곳!
 *
 * 이렇게 많은 곳을 수정해야 하고, 하나라도 빠뜨리면 버그 발생!
 */
object OcpViolationDemo {
    fun demonstrateProblem() {
        // 새 타입 추가 시 수정이 필요한 클래스들
        val classesToModify = listOf(
            "FeeCalculator",
            "PaymentValidator",
            "PaymentIconProvider",
            "PaymentReportGenerator",
            // ... 더 많을 수 있음
        )

        println("새 결제 타입 추가 시 수정해야 할 클래스: ${classesToModify.size}개")
        classesToModify.forEach { println("  - $it") }
    }
}
