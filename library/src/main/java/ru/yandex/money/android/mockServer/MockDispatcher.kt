package ru.yandex.money.android.mockServer

import android.content.Context
import com.google.gson.Gson
import com.yandex.money.api.model.CardBrand
import com.yandex.money.api.model.ExternalCard
import com.yandex.money.api.util.HttpHeaders
import com.yandex.money.api.util.MimeTypes
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import ru.yandex.money.android.utils.ResponseLoader
import ru.yandex.money.android.utils.ResponseType
import java.nio.charset.Charset
import java.util.*

class MockDispatcher(private val context: Context): Dispatcher() {

    private val gson = Gson()

    override fun dispatch(request: RecordedRequest): MockResponse {
        return when(request.requestUrl?.toUrl()?.path) {
            "/api/operation-details" -> {
                // TODO
                MockResponse().setResponseCode(200).setBody("https://yandex.ru/dev/money/doc/dg/reference/operation-history-docpage/")
            }
            "/api/request-external-payment" -> successResponse(ResponseType.REQUEST_EXTERNAL_PAYMENT, request)
            "/api/process-external-payment" -> successResponse(ResponseType.PROCESS_EXTERNAL_PAYMENT, request)
            "/api/instance-id" -> successResponse(ResponseType.INSTANCE_ID)
            else -> MockResponse().setResponseCode(400).setBody("Wrong request")
        }
    }

    private fun successResponse(type: ResponseType, request: RecordedRequest? = null): MockResponse =
            MockResponse()
                    .setResponseCode(200)
                    .addHeader(HttpHeaders.CONTENT_TYPE, MimeTypes.Text.JSON)
                    .applyBody(type, request)

    private fun MockResponse.applyBody(type: ResponseType, request: RecordedRequest? = null): MockResponse = setBody(createBody(type, request))

    private fun createBody(type: ResponseType, request: RecordedRequest? = null): String {
        val defaultResponse = ResponseLoader.loadResponse(context, type)
        return (request?.requestUrl.toString() + "?&" +  request?.body?.readString(Charset.defaultCharset())).toHttpUrlOrNull()?.let { url ->
            if (type == ResponseType.REQUEST_EXTERNAL_PAYMENT) {
                url.queryParameter("amount")?.let { amount ->
                    val title  = url.queryParameter("phone-number")?.let {
                        "Перевод по номеру телефона: $it"
                    } ?: "Перевод на кошелек: ${url.queryParameter("to")}"
                    return parseJson<RequestExternalPayment>(defaultResponse)
                            .copy(amount = amount, title = title)
                            .toJson()
                }
            }
            if (type == ResponseType.PROCESS_EXTERNAL_PAYMENT) {
                 val payment = parseJson<ProcessExternalPayment>(defaultResponse)
                 return payment
                         .copy(externalCard = ExternalCard.Builder()
                                 .setPanFragment(
                                         payment.externalCard.panFragment.let {
                                             it.replaceRange(it.length - 4, it.length, (1000..10000).random().toString())
                                         }
                                 )
                                 .setFundingSourceType(payment.externalCard.moneySourceToken)
                                 .setMoneySourceToken(UUID.randomUUID().toString())
                                 .setType(CardBrand.values().toList().shuffled().last())
                                 .create())
                         .toJson()

            }
            null
        } ?: defaultResponse
    }

    private inline fun <reified T> parseJson(json: String): T =
            gson.fromJson<T>(json, T::class.java)

    private fun Any.toJson(): String =
            gson.toJson(this)
}