package io.github.bokchidevchan.solid_ex.data.dto

import com.google.gson.annotations.SerializedName
import io.github.bokchidevchan.solid_ex.domain.model.Payment
import io.github.bokchidevchan.solid_ex.domain.model.PaymentType

/**
 * 결제 API 응답 DTO
 */
data class PaymentDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("amount")
    val amount: Int,
    @SerializedName("timestamp")
    val timestamp: Long,
    @SerializedName("type")
    val type: String,
    @SerializedName("category")
    val category: String
) {
    fun toDomain(): Payment {
        return Payment(
            id = id,
            title = title,
            amount = amount,
            timestamp = timestamp,
            type = PaymentType.fromString(type),
            category = category
        )
    }
}
