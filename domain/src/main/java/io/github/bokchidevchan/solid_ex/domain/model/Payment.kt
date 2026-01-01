package io.github.bokchidevchan.solid_ex.domain.model

/**
 * 결제 도메인 모델
 */
data class Payment(
    val id: String,
    val title: String,
    val amount: Int,
    val timestamp: Long,
    val type: PaymentType,
    val category: String,
    val fee: Int = 0
)

/**
 * 결제 타입
 */
enum class PaymentType(val label: String) {
    CARD("카드"),
    BANK("계좌이체"),
    CASH("현금"),
    GIFT("상품권");

    companion object {
        fun fromString(value: String): PaymentType {
            return entries.find { it.name == value } ?: CARD
        }
    }
}
