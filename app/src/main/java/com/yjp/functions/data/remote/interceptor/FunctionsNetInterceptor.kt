package com.yjp.functions.data.remote.interceptor

import android.util.Log
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject

class FunctionsNetInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val (requestToProceed, requestLog) = buildLoggedRequest(originalRequest)

        Log.d(TAG, "${originalRequest.method} ${originalRequest.url}")
        Log.d(TAG, "Headers:\n${formatHeaders(originalRequest.headers)}")
        Log.d(TAG, "Request:\n$requestLog")

        val response = chain.proceed(requestToProceed)
        logResponse(response)

        return response
    }

    private fun buildLoggedRequest(request: Request): Pair<Request, String> {
        val requestBody = request.body
        if (requestBody == null) {
            return request to formatQueryParams(request.url.query)
        }

        val buffer = Buffer()
        requestBody.writeTo(buffer)
        val bodyString = buffer.readUtf8()

        val loggedRequest = request.newBuilder()
            .method(request.method, bodyString.toRequestBody(requestBody.contentType()))
            .build()

        return loggedRequest to prettyJson(bodyString)
    }

    private fun logResponse(response: Response) {
        val responseBodyString = response.peekBody(MAX_LOG_BODY_BYTES).string()

        Log.d(TAG, "Response ${response.code} ${response.request.url}")
        Log.d(TAG, "Response Headers:\n${formatHeaders(response.headers)}")
        Log.d(TAG, "Response:\n${prettyJson(responseBodyString)}")
    }

    private fun formatHeaders(headers: Headers): String {
        if (headers.size == 0) {
            return EMPTY_BODY
        }

        val jsonObject = JSONObject()
        for (i in 0 until headers.size) {
            jsonObject.put(headers.name(i), headers.value(i))
        }
        return jsonObject.toString(INDENT_SPACES)
    }

    private fun formatQueryParams(query: String?): String {
        if (query.isNullOrBlank()) {
            return EMPTY_BODY
        }

        val jsonObject = JSONObject()
        query.split("&").forEach { param ->
            val keyValue = param.split("=", limit = 2)
            if (keyValue.size == 2) {
                jsonObject.put(keyValue[0], keyValue[1])
            }
        }

        return jsonObject.toString(INDENT_SPACES)
    }

    private fun prettyJson(raw: String): String {
        if (raw.isBlank()) {
            return EMPTY_BODY
        }

        return try {
            val trimmed = raw.trim()
            when {
                trimmed.startsWith("{") -> JSONObject(trimmed).toString(INDENT_SPACES)
                trimmed.startsWith("[") -> JSONArray(trimmed).toString(INDENT_SPACES)
                else -> raw
            }
        } catch (_: Exception) {
            raw
        }
    }

    companion object {
        private const val TAG = "FunctionsNet"
        private const val INDENT_SPACES = 2
        private const val EMPTY_BODY = "(empty)"
        private const val MAX_LOG_BODY_BYTES = 1024L * 1024L
    }
}
