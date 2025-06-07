package com.lollipop.http.request

import okhttp3.HttpUrl
import okhttp3.Request

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class HttpBuilderDsl

class HttpRequestBuilder(url: HttpUrl) {

    var currentUrl = url
        private set

    val requestBuilder = Request.Builder().apply {
        url(url)
    }

    fun updateUrl(url: HttpUrl) {
        currentUrl = url
        requestBuilder.url(url)
    }

}


