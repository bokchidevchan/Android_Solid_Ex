package io.github.bokchidevchan.solid_ex.domain.repository

import io.github.bokchidevchan.solid_ex.domain.model.Payment
import io.github.bokchidevchan.solid_ex.domain.model.PaymentType

/**
 * 결제 데이터 저장소 인터페이스
 * DIP 원칙: domain 모듈은 이 인터페이스에만 의존하고, 구현체는 data 모듈에서 제공
 */
interface PaymentRepository {
    suspend fun getPayments(): List<Payment>
    suspend fun getPaymentsByType(type: PaymentType): List<Payment>
    suspend fun getPaymentById(id: String): Payment?
}
