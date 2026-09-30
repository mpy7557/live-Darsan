package com.example.service

import com.example.data.model.OfferingRecord
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

sealed class PaymentState {
    object Idle : PaymentState()
    data class Processing(val orderId: String, val amount: Int) : PaymentState()
    data class Success(val record: OfferingRecord) : PaymentState()
    data class Error(val message: String) : PaymentState()
}

class RazorpayService {

    private val _apiKey = MutableStateFlow("rzp_test_DarshanLive2026")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    fun updateApiKey(newKey: String) {
        if (newKey.isNotBlank()) {
            _apiKey.value = newKey.trim()
        }
    }

    suspend fun processOffering(
        amount: Int,
        category: String,
        templeName: String,
        sevakName: String,
        devoteeName: String
    ): Result<OfferingRecord> {
        val orderId = "order_${UUID.randomUUID().toString().take(12)}"
        _paymentState.value = PaymentState.Processing(orderId, amount)

        // Simulate Razorpay secure checkout handshake
        delay(1200)

        val paymentId = "pay_${UUID.randomUUID().toString().take(14)}"
        val record = OfferingRecord(
            id = UUID.randomUUID().toString(),
            templeName = templeName,
            sevakName = sevakName,
            devoteeName = devoteeName,
            amount = amount,
            category = category,
            paymentId = paymentId,
            orderId = orderId,
            status = "SUCCESS",
            timestamp = System.currentTimeMillis()
        )

        _paymentState.value = PaymentState.Success(record)
        return Result.success(record)
    }

    fun resetState() {
        _paymentState.value = PaymentState.Idle
    }
}
