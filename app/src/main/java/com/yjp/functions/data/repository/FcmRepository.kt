package com.yjp.functions.data.repository

import android.content.Context
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.yjp.functions.BuildConfig
import com.yjp.functions.data.remote.result.FunctionsResult
import com.yjp.functions.util.FunctionsLog
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/** FCM HTTP v1 API로 푸시를 보내는 Repository */
@Singleton
class FcmRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient,
    private val gson: Gson,
) {

    /**
     * 푸시 전송 진입점
     * 1) Google OAuth 토큰 발급 → 2) FCM API 호출
     */
    suspend fun sendPush(
        deviceToken: String,
        title: String,
        body: String,
    ): FunctionsResult<Unit> {
        return try {
            val account = loadServiceAccount()
            val accessToken = fetchAccessToken(account)
            sendMessage(
                projectId = account.projectId,
                accessToken = accessToken,
                deviceToken = deviceToken,
                title = title,
                body = body,
            )
            FunctionsResult.Success(Unit)
        } catch (e: Exception) {
            FunctionsResult.Fail(
                message = e.message ?: "푸시 전송에 실패했습니다",
                throwable = e,
            )
        }
    }

    /** 서비스 계정으로 Google access token 발급 */
    private fun fetchAccessToken(account: ServiceAccount): String {
        // 서비스 계정 JSON으로 JWT 만들어서 OAuth에 제출
        val jwt = createJwt(account.clientEmail, account.privateKey)

        val body = "grant_type=$OAUTH_GRANT_TYPE&assertion=$jwt"
            .toRequestBody("application/x-www-form-urlencoded".toMediaType())
        val request = Request.Builder()
            .url(TOKEN_URL)
            .post(body)
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throwHttpError("액세스 토큰 발급", response.code, responseBody)
            }
            return JSONObject(responseBody).getString("access_token")
        }
    }

    /** FCM messages:send API로 알림 전송 */
    private fun sendMessage(
        projectId: String,
        accessToken: String,
        deviceToken: String,
        title: String,
        body: String,
    ) {
        val payload = mapOf(
            "message" to mapOf(
                "token" to deviceToken,
                "notification" to mapOf(
                    "title" to title,
                    "body" to body,
                ),
                "android" to mapOf(
                    "notification" to mapOf(
                        "channel_id" to BuildConfig.FCM_NOTIFICATION_CHANNEL_ID,
                    ),
                ),
            ),
        )
        val request = Request.Builder()
            .url("https://fcm.googleapis.com/v1/projects/$projectId/messages:send")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", JSON_MEDIA)
            .post(gson.toJson(payload).toRequestBody(JSON_MEDIA.toMediaType()))
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val responseBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throwHttpError("푸시 전송", response.code, responseBody)
            }
            FunctionsLog.d("FCM 전송 성공: $responseBody")
        }
    }

    /** Google API 에러 응답을 읽기 쉽게 변환 */
    private fun throwHttpError(action: String, code: Int, responseBody: String): Nothing {
        val detail = parseGoogleErrorMessage(responseBody)
        FunctionsLog.e("$action 실패 ($code): $responseBody")
        val hint = if (code == 403) {
            " (FCM API 활성화 및 서비스 계정 권한 확인 필요)"
        } else {
            ""
        }
        throw IllegalStateException("$action 실패 ($code): $detail$hint")
    }

    private fun parseGoogleErrorMessage(responseBody: String): String {
        return try {
            JSONObject(responseBody)
                .optJSONObject("error")
                ?.optString("message")
                ?.takeIf { it.isNotBlank() }
                ?: responseBody
        } catch (_: Exception) {
            responseBody.ifBlank { "응답 본문 없음" }
        }
    }

    /** assets의 서비스 계정 JSON 읽기 */
    private fun loadServiceAccount(): ServiceAccount {
        return context.assets.open(SERVICE_ACCOUNT_ASSET).bufferedReader().use { reader ->
            gson.fromJson(reader, ServiceAccount::class.java)
        }
    }

    /** OAuth 인증용 JWT 생성 (Google이 요구하는 형식) */
    private fun createJwt(clientEmail: String, privateKeyPem: String): String {
        val nowSeconds = System.currentTimeMillis() / 1000
        val header = base64Url("""{"alg":"RS256","typ":"JWT"}""")
        val claim = base64Url(
            JsonObject().apply {
                addProperty("iss", clientEmail)
                addProperty("scope", FCM_SCOPE)
                addProperty("aud", TOKEN_URL)
                addProperty("iat", nowSeconds)
                addProperty("exp", nowSeconds + 3600)
            }.toString(),
        )
        // JWT = header.claim.서명  (서명 없으면 Google이 거부함)
        val signingInput = "$header.$claim"
        val signature = base64Url(signRs256(signingInput, privateKeyPem))
        return "$signingInput.$signature"
    }

    /**
     * JWT의 header+claim 문자열에 서명을 붙이기 위한 함수
     *
     * Google OAuth는 "이 요청이 진짜 우리 서비스 계정이 보낸 거 맞아?" 를 private key 서명으로 확인함
     */
    private fun signRs256(data: String, privateKeyPem: String): ByteArray {
        // JSON에 들어있는 PEM 형식 private key → 바이너리 key로 변환
        val keyBytes = privateKeyPem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\n", "")
            .replace("\n", "")
            .replace(" ", "")
            .let { Base64.decode(it, Base64.DEFAULT) }

        val privateKey = KeyFactory.getInstance("RSA")
            .generatePrivate(PKCS8EncodedKeySpec(keyBytes))
        // data(header.claim)를 private key로 서명 → JWT 3번째 조각(서명값)
        return Signature.getInstance("SHA256withRSA").apply {
            initSign(privateKey)
            update(data.toByteArray(Charsets.UTF_8))
        }.sign()
    }

    private fun base64Url(value: String): String =
        base64Url(value.toByteArray(Charsets.UTF_8))

    /** JWT용 Base64 URL 인코딩 */
    private fun base64Url(bytes: ByteArray): String =
        Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)

    /** 서비스 계정 JSON에서 필요한 필드만 매핑 */
    private data class ServiceAccount(
        @SerializedName("project_id") val projectId: String = BuildConfig.FCM_PROJECT_ID,
        @SerializedName("client_email") val clientEmail: String,
        @SerializedName("private_key") val privateKey: String,
    )

    companion object {
        private const val SERVICE_ACCOUNT_ASSET = "fcm-service-account.json"
        private const val FCM_SCOPE = "https://www.googleapis.com/auth/firebase.messaging"
        private const val TOKEN_URL = "https://oauth2.googleapis.com/token"
        private const val OAUTH_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:jwt-bearer"
        private const val JSON_MEDIA = "application/json; charset=UTF-8"
    }
}
