package com.lollipop.http.response

import okhttp3.Call
import okhttp3.Response

class ResponseParser(
    val call: Call,
    val response: Response
) {

    val protocol by lazy { response.protocol }

    val code by lazy { response.code }

    val message by lazy { response.message }

    val headers by lazy { response.headers }

    val body by lazy { response.body }

}