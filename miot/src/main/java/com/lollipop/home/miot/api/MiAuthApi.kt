package com.lollipop.home.miot.api

import com.lollipop.http.HTTP
import com.lollipop.http.request.PostForm
import com.lollipop.http.request.call
import com.lollipop.http.response.JsonResponse
import com.lollipop.http.response.json
import com.lollipop.http.safe.SafeResult

class MiAuthApi {

    /**
     * - client_id	long	申请应用时分配的应用 ID，可以在应用详情页获取
     * - redirect_uri	string	回调地址, 必须和申请应用是填写的一致(参数部分可不一致)
     * - client_secret	string	申请应用时分配的 AppSecret
     * - grant_type	string	这里 grant_type=authorization_code
     * - code	string	第1小节中拿到的授权码，有效期为 10 分钟且只能使用一次
     */
    fun accessToken(
        clientId: String,
        clientSecret: String,
        code: String,
        redirectUri: String,
        callback: (SafeResult<MiAuthTokenInfo>) -> Unit
    ) {
        HTTP.with("https://account.xiaomi.com/oauth2/token") {
            PostForm {
                "client_id" to clientId
                "redirect_uri" to redirectUri
                "client_secret" to clientSecret
                "grant_type" to "authorization_code"
                "code" to code
            }
        }.call { result ->
            callback(result.json<MiAuthTokenInfo>())
        }
    }
}

/**
 * {
 *   "access_token": "access token value",
 *   "expires_in": 259200,
 *   "refresh_token": "refresh token value",
 *   "scope": "scope value",
 *   "token_type ": "mac",
 *   "mac_key ": "mac key value",
 *   "mac_algorithm": "HmacSHA1",
 *   "openId":"2.0XXXXXXXXX",
 *    "union_id": "union_id value"
 * }
 */
class MiAuthTokenInfo : JsonResponse() {

    val token by stringValue("access_token")
    val refreshToken by stringValue("refresh_token")
    val expiresIn by longValue("expires_in")
    val openId by stringValue("openId")
    val unionId by stringValue("union_id")
    val scope by stringValue("scope")
    val tokenType by stringValue("token_type")
    val macKey by stringValue("mac_key")
    val macAlgorithm by stringValue("mac_algorithm")

}
