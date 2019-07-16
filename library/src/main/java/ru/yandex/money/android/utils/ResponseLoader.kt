package ru.yandex.money.android.utils

import android.content.Context

object ResponseLoader {
    fun loadResponse(context: Context, type: ResponseType): String {
        return with(context.assets.open(type.path)) {
            this.bufferedReader().readText()
        }
    }
}

enum class ResponseType(val path: String) {
    INSTANCE_ID("responses/instance-id.json"),
    REQUEST_EXTERNAL_PAYMENT("responses/request-external-payment.json"),
    PROCESS_EXTERNAL_PAYMENT("responses/process-external-payment.json"),
    REQUEST_PAYMENT("responses/request-payment.json")
}