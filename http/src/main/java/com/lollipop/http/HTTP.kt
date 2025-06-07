package com.lollipop.http

import android.util.Log
import com.lollipop.http.request.Header
import com.lollipop.http.request.HttpBuilderDsl
import com.lollipop.http.request.HttpRequestBuilder
import com.lollipop.http.request.Params
import com.lollipop.http.request.PostJson
import com.lollipop.http.request.call
import com.lollipop.http.response.JsonResponse
import com.lollipop.http.response.json
import com.lollipop.http.safe.onError
import com.lollipop.http.safe.onSuccess
import com.lollipop.http.safe.safeMap
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import java.net.URL
import java.util.concurrent.CopyOnWriteArrayList

object HTTP {

    private val preprocessingList = CopyOnWriteArrayList<Preprocessing>()
    private val reprocessingList = CopyOnWriteArrayList<Reprocessing>()

    private val clientPreprocessingList = CopyOnWriteArrayList<ClientPreprocessing>()

    fun preprocessing(preprocessing: Preprocessing) {
        preprocessingList.add(preprocessing)
    }

    fun preprocessing(preprocessing: ClientPreprocessing) {
        clientPreprocessingList.add(preprocessing)
    }

    fun reprocessing(reprocessing: Reprocessing) {
        this.reprocessingList.add(reprocessing)
    }

    val httpClient by lazy { newClient() }

    fun newClient(): OkHttpClient {
        return preprocessing(OkHttpClient())
    }

    private fun preprocessing(client: OkHttpClient): OkHttpClient {
        var newClient = client
        for (preprocessing in clientPreprocessingList) {
            newClient = preprocessing.preprocessing(newClient)
        }
        return newClient
    }

    fun with(
        url: String,
        builder: @HttpBuilderDsl HttpRequestBuilder.() -> Unit
    ): HttpRequestBuilder {
        return with(url.toHttpUrl(), builder)
    }

    fun with(
        url: URL,
        builder: @HttpBuilderDsl HttpRequestBuilder.() -> Unit
    ): HttpRequestBuilder {
        return with(url.toString().toHttpUrl(), builder)
    }

    fun with(
        url: HttpUrl,
        builder: @HttpBuilderDsl HttpRequestBuilder.() -> Unit
    ): HttpRequestBuilder {
        val requestBuilder = HttpRequestBuilder(url)
        for (preprocessing in preprocessingList) {
            try {
                preprocessing.preprocessing(requestBuilder)
            } catch (e: Throwable) {
                Log.e("HTTP", "preprocessing error", e)
            }
        }
        requestBuilder.apply(builder)
        for (reprocessing in reprocessingList) {
            try {
                reprocessing.reprocessing(requestBuilder)
            } catch (e: Throwable) {
                Log.e("HTTP", "reprocessing error", e)
            }
        }
        return requestBuilder
    }

    fun interface Preprocessing {

        fun preprocessing(request: HttpRequestBuilder)

    }

    fun interface Reprocessing {
        fun reprocessing(request: HttpRequestBuilder)
    }

    fun interface ClientPreprocessing {
        fun preprocessing(client: OkHttpClient): OkHttpClient
    }

}
