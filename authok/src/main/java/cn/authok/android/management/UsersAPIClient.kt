package cn.authok.android.management

import androidx.annotation.VisibleForTesting
import cn.authok.android.Authok
import cn.authok.android.AuthokException
import cn.authok.android.authentication.ParameterBuilder
import cn.authok.android.request.ErrorAdapter
import cn.authok.android.request.JsonAdapter
import cn.authok.android.request.NetworkingClient
import cn.authok.android.request.Request
import cn.authok.android.request.internal.BaseRequest
import cn.authok.android.request.internal.GsonAdapter
import cn.authok.android.request.internal.GsonAdapter.Companion.forListOf
import cn.authok.android.request.internal.GsonAdapter.Companion.forMap
import cn.authok.android.request.internal.GsonProvider
import cn.authok.android.request.internal.RequestFactory
import cn.authok.android.result.UserIdentity
import cn.authok.android.result.UserProfile
import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException
import java.io.Reader

/**
 * API client for Authok Management API.
 * ```
 * val authok = Authok("your_client_id", "your_domain")
 * val client = UsersAPIClient(authok)
 * ```
 *
 * @see [Auth API docs](https://docs.authok.cn/docs/api/management/v1)
 */
public class UsersAPIClient @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) internal constructor(
    private val authok: Authok,
    private val factory: RequestFactory<ManagementException>,
    private val gson: Gson
) {
    /**
     * Creates a new API client instance providing the Authok account info and the access token.
     *
     * @param authok            account information
     * @param token            of the primary identity
     */
    public constructor(
        authok: Authok,
        token: String
    ) : this(
        authok,
        factoryForToken(token, authok.networkingClient),
        GsonProvider.gson
    )

    public val clientId: String
        get() = authok.clientId
    public val baseURL: String
        get() = authok.getDomainUrl()

    /**
     * Link a user identity calling ['/api/v1/users/:primaryUserId/identities'](https://docs.authok.cn/docs/link-accounts#the-management-api) endpoint
     * Example usage:
     * ```
     * client.link("{authok primary user id}", "{user secondary token}")
     *     .start(object: Callback<List<UserIdentity>, ManagementException> {
     *         override fun onSuccess(result: List<UserIdentity>) { }
     *         override fun onFailure(error: ManagementException) { }
     * })
     * ```
     *
     * @param primaryUserId  of the identity to link
     * @param secondaryToken of the secondary identity obtained after login
     * @return a request to start
     */
    public fun link(
        primaryUserId: String,
        secondaryToken: String
    ): Request<List<UserIdentity>, ManagementException> {
        val url = authok.getDomainUrl().toHttpUrl().newBuilder()
            .addPathSegment(API_PATH)
            .addPathSegment(V1_PATH)
            .addPathSegment(USERS_PATH)
            .addPathSegment(primaryUserId)
            .addPathSegment(IDENTITIES_PATH)
            .build()
        val parameters = ParameterBuilder.newBuilder()
            .set(LINK_WITH_KEY, secondaryToken)
            .asDictionary()
        val userIdentitiesAdapter: JsonAdapter<List<UserIdentity>> = forListOf(
            UserIdentity::class.java, gson
        )
        return factory.post(url.toString(), userIdentitiesAdapter)
            .addParameters(parameters)
    }

    /**
     * Unlink a user identity calling ['/api/v1/users/:primaryToken/identities/secondaryProvider/secondaryUserId'](https://docs.authok.cn/docs/link-accounts#unlinking-accounts) endpoint
     * Example usage:
     * ```
     * client.unlink("{authok primary user id}", {authok secondary user id}, "{secondary provider}")
     *     .start(object: Callback<List<UserIdentity>, ManagementException> {
     *         override fun onSuccess(result: List<UserIdentity>) { }
     *         override fun onFailure(error: ManagementException) {}
     * })
     * ```
     *
     * @param primaryUserId     of the primary identity to unlink
     * @param secondaryUserId   of the secondary identity you wish to unlink from the main one.
     * @param secondaryProvider of the secondary identity you wish to unlink from the main one.
     * @return a request to start
     */
    public fun unlink(
        primaryUserId: String,
        secondaryUserId: String,
        secondaryProvider: String
    ): Request<List<UserIdentity>, ManagementException> {
        val url = authok.getDomainUrl().toHttpUrl().newBuilder()
            .addPathSegment(API_PATH)
            .addPathSegment(V1_PATH)
            .addPathSegment(USERS_PATH)
            .addPathSegment(primaryUserId)
            .addPathSegment(IDENTITIES_PATH)
            .addPathSegment(secondaryProvider)
            .addPathSegment(secondaryUserId)
            .build()
        val userIdentitiesAdapter: JsonAdapter<List<UserIdentity>> = forListOf(
            UserIdentity::class.java, gson
        )
        return factory.delete(url.toString(), userIdentitiesAdapter)
    }

    /**
     * Update the user_metadata calling ['/api/v1/users/:userId'](https://docs.authok.cn/docs/api/management/v1#!/Users/patch_users_by_id) endpoint
     * Example usage:
     * ```
     * client.updateMetadata("{user id}", "{user metadata}")
     *     .start(object: Callback<UserProfile, ManagementException> {
     *         override fun onSuccess(result: UserProfile) { }
     *         override fun onFailure(error: ManagementException) { }
     * })
     * ```
     *
     * @param userId       of the primary identity to unlink
     * @param userMetadata to merge with the existing one
     * @return a request to start
     */
    public fun updateMetadata(
        userId: String,
        userMetadata: Map<String, Any?>
    ): Request<UserProfile, ManagementException> {
        val url = authok.getDomainUrl().toHttpUrl().newBuilder()
            .addPathSegment(API_PATH)
            .addPathSegment(V1_PATH)
            .addPathSegment(USERS_PATH)
            .addPathSegment(userId)
            .build()
        val userProfileAdapter: JsonAdapter<UserProfile> = GsonAdapter(
            UserProfile::class.java, gson
        )
        val patch = factory.patch(
            url.toString(),
            userProfileAdapter
        ) as BaseRequest<UserProfile, ManagementException>
        patch.addParameter(USER_METADATA_KEY, userMetadata)
        return patch
    }

    /**
     * Get the User Profile calling ['/api/v1/users/:userId'](https://docs.authok.cn/docs/api/management/v1#!/Users/get_users_by_id) endpoint
     * Example usage:
     * ```
     * client.getProfile("{user id}")
     *     .start(object: Callback<UserProfile, ManagementException> {
     *         override fun onSuccess(result: UserProfile) { }
     *         override fun onFailure(error: ManagementException) { }
     * })
     * ```
     *
     * @param userId identity of the user
     * @return a request to start
     */
    public fun getProfile(userId: String): Request<UserProfile, ManagementException> {
        val url = authok.getDomainUrl().toHttpUrl().newBuilder()
            .addPathSegment(API_PATH)
            .addPathSegment(V1_PATH)
            .addPathSegment(USERS_PATH)
            .addPathSegment(userId)
            .build()
        val userProfileAdapter: JsonAdapter<UserProfile> = GsonAdapter(
            UserProfile::class.java, gson
        )
        return factory.get(url.toString(), userProfileAdapter)
    }

    private companion object {
        private const val LINK_WITH_KEY = "link_with"
        private const val API_PATH = "api"
        private const val V1_PATH = "v1"
        private const val USERS_PATH = "users"
        private const val IDENTITIES_PATH = "identities"
        private const val USER_METADATA_KEY = "user_metadata"

        private fun createErrorAdapter(): ErrorAdapter<ManagementException> {
            val mapAdapter = forMap(GsonProvider.gson)
            return object : ErrorAdapter<ManagementException> {
                override fun fromRawResponse(
                    statusCode: Int,
                    bodyText: String,
                    headers: Map<String, List<String>>
                ): ManagementException {
                    return ManagementException(bodyText, statusCode)
                }

                @Throws(IOException::class)
                override fun fromJsonResponse(
                    statusCode: Int,
                    reader: Reader
                ): ManagementException {
                    val values = mapAdapter.fromJson(reader)
                    return ManagementException(values)
                }

                override fun fromException(cause: Throwable): ManagementException {
                    return ManagementException(
                        "Something went wrong",
                        AuthokException("Something went wrong", cause)
                    )
                }
            }
        }

        private fun factoryForToken(
            token: String,
            client: NetworkingClient
        ): RequestFactory<ManagementException> {
            val factory = RequestFactory(client, createErrorAdapter())
            factory.setHeader("Authorization", "Bearer $token")
            return factory
        }
    }

    init {
        factory.setAuthokClientInfo(authok.authokUserAgent.value)
    }
}