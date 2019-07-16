package ru.yandex.money.android.mockServer

import com.yandex.money.api.net.providers.HostsProvider

class DefaultMockServerHostsProvider(private val port: String = "8080") : HostsProvider {
    override fun getMoney(): String = "http://127.0.0.1:$port";

    override fun getMoneyApi(): String  = "$money/api"

    override fun getWebUrl(): String = money

    override fun getPaymentApi(): String = moneyApi

    override fun getMobileMoney(): String = money

}