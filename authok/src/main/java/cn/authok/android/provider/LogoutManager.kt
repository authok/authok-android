package cn.authok.android.provider

import android.content.Context
import android.net.Uri
import android.util.Log
import cn.authok.android.Authok
import cn.authok.android.authentication.AuthenticationException
import cn.authok.android.callback.Callback
import java.util.*

internal class LogoutManager(
    private val account: Authok,
    private val callback: Callback<Void?, AuthenticationException>,
    returnToUrl: String,
    ctOptions: CustomTabsOptions,
    federated: Boolean = false
) : ResumableManager() {
    private val parameters: MutableMap<String, String>
    private val ctOptions: CustomTabsOptions
    fun startLogout(context: Context) {
        addClientParameters(parameters)
        val uri = buildLogoutUri()
        AuthenticationActivity.authenticateUsingBrowser(context, uri, ctOptions)
    }

    public override fun resume(result: AuthorizeResult): Boolean {
        if (result.isCanceled) {
            val exception = AuthenticationException(
                AuthenticationException.ERROR_VALUE_AUTHENTICATION_CANCELED,
                "The user closed the browser app so the logout was cancelled."
            )
            callback.onFailure(exception)
        } else {
            callback.onSuccess(null)
        }
        return true
    }

    private fun buildLogoutUri(): Uri {
        val logoutUri = Uri.parse(account.logoutUrl)
        val builder = logoutUri.buildUpon()
        for ((key, value) in parameters) {
            builder.appendQueryParameter(key, value)
        }
        val uri = builder.build()
        Log.d(TAG, "Using the following Logout URI: $uri")
        return uri
    }

    private fun addClientParameters(parameters: MutableMap<String, String>) {
        parameters[KEY_USER_AGENT] = account.authokUserAgent.value
        parameters[KEY_CLIENT_ID] = account.clientId
    }

    companion object {
        private val TAG = LogoutManager::class.java.simpleName
        private const val KEY_CLIENT_ID = "client_id"
        private const val KEY_USER_AGENT = "authokClient"
        private const val KEY_RETURN_TO_URL = "return_to"
        private const val KEY_FEDERATED = "federated"
    }

    init {
        parameters = HashMap()
        parameters[KEY_RETURN_TO_URL] = returnToUrl
        if (federated) {
            // null or empty values are not included in the request
            parameters[KEY_FEDERATED] = "1"
        }
        this.ctOptions = ctOptions
    }
}