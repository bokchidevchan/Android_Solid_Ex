package io.github.bokchidevchan.solid_ex.data.api

import io.github.bokchidevchan.solid_ex.data.dto.PaymentDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 결제 API 인터페이스
 */
interface PaymentApi {
    @GET("payments")
    suspend fun getPayments(): List<PaymentDto>

    @GET("payments")
    suspend fun getPaymentsByType(@Query("type") type: String): List<PaymentDto>

    @GET("payments/{id}")
    suspend fun getPaymentById(@retrofit2.http.Path("id") id: String): PaymentDto
}
