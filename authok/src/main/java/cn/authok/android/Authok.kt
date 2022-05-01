package cn.authok.android

import android.content.Context
import cn.authok.android.request.DefaultClient
import cn.authok.android.request.NetworkingClient
import cn.authok.android.util.AuthokUserAgent
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.util.*

/**
 * Represents your Authok account information (clientId &amp; domain),
 * and it's used to obtain clients for Authok's APIs.
 *
 * ```
 * val authok = Authok("YOUR_CLIENT_ID", "YOUR_DOMAIN")
 *```
 *
 * This SDK only supports OIDC-Conformant clients, and will use Authok's current authentication pipeline.
 * For more information, please see the [OIDC adoption guide](https://docs.authok.cn/docs/api-auth/tutorials/adoption).
 *
 * @param clientId            of your Authok application
 * @param domain              of your Authok account
 * @param configurationDomain where Authok's configuration will be fetched, change it if using an on-premise Authok server. By default is Authok public cloud.
 */
public open class Authok @JvmOverloads constructor(
    /**
     * @return your Authok application client identifier
     */
    public val clientId: String, domain: String, configurationDomain: String? = null
) {
    private val domainUrl: HttpUrl?
    private val configurationUrl: HttpUrl

    /**
     * @return Authok user agent information sent in every request
     */
    public var authokUserAgent: AuthokUserAgent

    /**
     * The networking client instance used to make HTTP requests.
     */
    public var networkingClient: NetworkingClient = DefaultClient()

    /**
     * Creates a new Authok instance with the 'cn_authok_client_id' and 'cn_authok_domain' values
     * defined in the project String resources file.
     * If the values are not found, IllegalArgumentException will raise.
     *
     * @param context a valid context
     */
    public constructor(context: Context) : this(
        getResourceFromContext(context, "cn_authok_client_id"),
        getResourceFromContext(context, "cn_authok_domain")
    )

    /**
     * @return your Authok account domain url
     */
    public fun getDomainUrl(): String {
        return domainUrl.toString()
    }

    /**
     * @return your account configuration url
     */
    public fun getConfigurationUrl(): String {
        return configurationUrl.toString()
    }

    /**
     * Obtain the authorize URL for the current domain
     *
     * @return Url to call to perform the web flow of OAuth
     */
    public val authorizeUrl: String
        get() = domainUrl!!.newBuilder()
            .addEncodedPathSegment("authorize")
            .build()
            .toString()

    /**
     * Obtain the logout URL for the current domain
     *
     * @return Url to call to perform the web logout
     */
    public val logoutUrl: String
        get() = domainUrl!!.newBuilder()
            .addEncodedPathSegment("v1")
            .addEncodedPathSegment("logout")
            .build()
            .toString()

    private fun ensureValidUrl(url: String?): HttpUrl? {
        if (url == null) {
            return null
        }
        val normalizedUrl = url.lowercase(Locale.ROOT)
        require(!normalizedUrl.startsWith("http://")) { "Invalid domain url: '$url'. Only HTTPS domain URLs are supported. If no scheme is passed, HTTPS will be used." }
        val safeUrl =
            if (normalizedUrl.startsWith("https://")) normalizedUrl else "https://$normalizedUrl"
        return safeUrl.toHttpUrlOrNull()
    }

    private companion object {
        private fun getResourceFromContext(context: Context, resName: String): String {
            val stringRes = context.resources.getIdentifier(resName, "string", context.packageName)
            require(stringRes != 0) {
                String.format(
                    "The 'R.string.%s' value it's not defined in your project's resources file.",
                    resName
                )
            }
            return context.getString(stringRes)
        }
    }

    init {
        domainUrl = ensureValidUrl(domain)
        requireNotNull(domainUrl) { String.format("Invalid domain url: '%s'", domain) }
        configurationUrl = ensureValidUrl(configurationDomain) ?: domainUrl
        authokUserAgent = AuthokUserAgent()
    }
}