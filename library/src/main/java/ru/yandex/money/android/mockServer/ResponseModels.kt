package ru.yandex.money.android.mockServer

import com.google.gson.annotations.SerializedName
import com.yandex.money.api.model.ExternalCard

data class RequestExternalPayment(
        val status: String,
        @SerializedName("request_id")
        val requestID: String,
        @SerializedName("contract_amount")
        val amount: String,
        val title: String
)

data class ProcessExternalPayment(
        val status: String,
        @SerializedName("invoice_id")
        val invoiceId: String,
        @SerializedName("money_source")
        val externalCard: ExternalCard
)