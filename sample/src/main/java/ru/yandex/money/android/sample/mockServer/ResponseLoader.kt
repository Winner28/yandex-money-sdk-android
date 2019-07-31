package ru.yandex.money.android.mockServer

import android.content.Context

object ResponseLoader {
    fun loadResponse(context: Context, type: ResponseType): String =
            with(context.assets.open(type.path)) { bufferedReader().readText() }
}