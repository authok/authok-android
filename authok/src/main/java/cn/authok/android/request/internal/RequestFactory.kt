package cn.authok.android.request.internal

import androidx.annotation.VisibleForTesting
import cn.authok.android.AuthokException
import cn.authok.android.request.*
import cn.authok.android.util.AuthokUserAgent
import java.io.Reader
import java.util.*

internal class RequestFactory<U : AuthokException> internal constructor(
    private val client: NetworkingClient,
    private val errorAdapter: ErrorAdapter<U>
) {

    private companion object {
        private const val DEFAULT_LOCALE_IF_MISSING = "en_US"
        private const val ACCEPT_LANGUAGE_HEADER = "Accept-Language"
        private const val AUTHOK_CLIENT_INFO_HEADER = AuthokUserAgent.HEADER_NAME

        val defaultLocale: String
            get() {
                val language = Locale.getDefault().toString()
                return if (language.isNotEmpty()) language else DEFAULT_LOCALE_IF_MISSING
            }
    }

    private val baseHeaders = mutableMapOf(Pair(ACCEPT_LANGUAGE_HEADER, defaultLocale))

    fun <T> post(
        url: String,
        resultAdapter: JsonAdapter<T>
    ): Request<T, U> = setupRequest(HttpMethod.POST, url, resultAdapter, errorAdapter)

    fun post(url: String): Request<Void?, U> =
        this.post(url, object : JsonAdapter<Void?> {
            override fun fromJson(reader: Reader): Void? {
                return null
            }
        })

    fun <T> patch(
        url: String,
        resultAdapter: JsonAdapter<T>
    ): Request<T, U> = setupRequest(HttpMethod.PATCH, url, resultAdapter, errorAdapter)

    fun <T> delete(
        url: String,
        resultAdapter: JsonAdapter<T>
    ): Request<T, U> = setupRequest(HttpMethod.DELETE, url, resultAdapter, errorAdapter)

    fun <T> get(
        url: String,
        resultAdapter: JsonAdapter<T>
    ): Request<T, U> = setupRequest(HttpMethod.GET, url, resultAdapter, errorAdapter)

    fun setHeader(name: String, value: String) {
        baseHeaders[name] = value
    }

    fun setAuthokClientInfo(clientInfo: String) {
        baseHeaders[AUTHOK_CLIENT_INFO_HEADER] = clientInfo
    }

    @VisibleForTesting
    fun <T> createRequest(
        method: HttpMethod,
        url: String,
        client: NetworkingClient,
        resultAdapter: JsonAdapter<T>,
        errorAdapter: ErrorAdapter<U>,
        threadSwitcher: ThreadSwitcher
    ): Request<T, U> = BaseRequest(method, url, client, resultAdapter, errorAdapter, threadSwitcher)


    private fun <T> setupRequest(
        method: HttpMethod,
        url: String,
        resultAdapter: JsonAdapter<T>,
        errorAdapter: ErrorAdapter<U>
    ): Request<T, U> {
        val request =
            createRequest(
                method,
                url,
                client,
                resultAdapter,
                errorAdapter,
                CommonThreadSwitcher.getInstance()
            )
        baseHeaders.map { request.addHeader(it.key, it.value) }
        return request
    }

}