package com.lollipop.http.request

import com.lollipop.http.HTTP
import com.lollipop.http.response.ResponseParser
import com.lollipop.http.safe.SafeResult
import com.lollipop.http.safe.safe
import com.lollipop.http.safe.safeMap
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

fun HttpRequestBuilder.call(
    newClient: Boolean = false,
): SafeResult<ResponseParser> {
    return createRequestCall(newClient).safeMap {
        ResponseParser(it, it.execute())
    }
}

fun HttpRequestBuilder.call(
    newClient: Boolean = false,
    callback: RequestAsync
) {
    createRequestCall(newClient).safeMap {
        it.enqueue(RequestAsyncDelegate(callback))
    }
}

private fun HttpRequestBuilder.createRequestCall(newClient: Boolean): SafeResult<Call> {
    return safe {
        val client = if (newClient) {
            HTTP.newClient()
        } else {
            HTTP.httpClient
        }
        val request = requestBuilder.build()
        client.newCall(request)
    }
}

class RequestAsyncDelegate(private val responseCallback: RequestAsync) : Callback {


    override fun onFailure(call: Call, e: IOException) {
        responseCallback.onResponse(SafeResult.Error(e))
    }

    override fun onResponse(call: Call, response: Response) {
        responseCallback.onResponse(
            SafeResult.Success(
                ResponseParser(call, response)
            )
        )
    }

}

fun interface RequestAsync {
    fun onResponse(result: SafeResult<ResponseParser>)
}
