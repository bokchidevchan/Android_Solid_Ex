package io.github.bokchidevchan.solid_ex.data.repository

import io.github.bokchidevchan.solid_ex.domain.model.Payment
import io.github.bokchidevchan.solid_ex.domain.model.PaymentType
import io.github.bokchidevchan.solid_ex.domain.repository.PaymentRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * PaymentRepository 구현체
 * 실제 환경에서는 PaymentApi를 사용하지만, 예제에서는 Fake 데이터 사용
 */
class PaymentRepositoryImpl @Inject constructor() : PaymentRepository {

    // Fake 데이터 (실제로는 API 호출)
    private val fakePayments = listOf(
        Payment("1", "스타벅스", 5500, System.currentTimeMillis() - 86400000, PaymentType.CARD, "카페"),
        Payment("2", "CGV 영화", 15000, System.currentTimeMillis() - 172800000, PaymentType.CARD, "문화"),
        Payment("3", "편의점", 3200, System.currentTimeMillis() - 259200000, PaymentType.CASH, "생활"),
        Payment("4", "상품권 사용", 50000, System.currentTimeMillis() - 345600000, PaymentType.GIFT, "기타"),
        Payment("5", "배달의민족", 28000, System.currentTimeMillis() - 432000000, PaymentType.CARD, "식비"),
        Payment("6", "계좌이체", 100000, System.currentTimeMillis() - 518400000, PaymentType.BANK, "송금"),
    )

    override suspend fun getPayments(): List<Payment> {
        delay(500) // 네트워크 지연 시뮬레이션
        return fakePayments
    }

    override suspend fun getPaymentsByType(type: PaymentType): List<Payment> {
        delay(500)
        return fakePayments.filter { it.type == type }
    }

    override suspend fun getPaymentById(id: String): Payment? {
        delay(300)
        return fakePayments.find { it.id == id }
    }
}
