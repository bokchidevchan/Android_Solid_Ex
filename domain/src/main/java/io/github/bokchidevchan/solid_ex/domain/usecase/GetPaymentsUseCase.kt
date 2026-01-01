package io.github.bokchidevchan.solid_ex.domain.usecase

import io.github.bokchidevchan.solid_ex.domain.model.Payment
import io.github.bokchidevchan.solid_ex.domain.model.PaymentType
import io.github.bokchidevchan.solid_ex.domain.repository.PaymentRepository
import javax.inject.Inject

/**
 * 결제 내역 조회 UseCase
 * SRP 원칙: 결제 내역 조회라는 단일 책임만 수행
 */
class GetPaymentsUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(type: PaymentType? = null): Result<List<Payment>> {
        return runCatching {
            val payments = if (type == null) {
                repository.getPayments()
            } else {
                repository.getPaymentsByType(type)
            }
            // 정렬 정책: 최신순
            payments.sortedByDescending { it.timestamp }
        }
    }
}
