package com.lollipop.http

import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class HttpBuilderDsl

class HttpRequestBuilder(private val url: HttpUrl) {

    val requestBuilder = Request.Builder().apply {
        url(url)
    }

}


