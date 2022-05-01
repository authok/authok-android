package cn.authok.android.request

import cn.authok.android.AuthokException
import cn.authok.android.authentication.AuthenticationException
import cn.authok.android.callback.Callback
import cn.authok.android.result.Authentication
import cn.authok.android.result.Credentials
import cn.authok.android.result.UserProfile

/**
 * Request to fetch a profile after a successful authentication with Authok Authentication API
 */
public class ProfileRequest
/**
 * @param authenticationRequest the request that will output a pair of credentials
 * @param userInfoRequest       the /userinfo request that will be wrapped
 */(
    private val authenticationRequest: AuthenticationRequest,
    private val userInfoRequest: Request<UserProfile, AuthenticationException>
) : Request<Authentication, AuthenticationException> {
    /**
     * Adds additional parameters for the login request
     *
     * @param parameters as a non-null dictionary
     * @return itself
     */
    override fun addParameters(parameters: Map<String, String>): ProfileRequest {
        authenticationRequest.addParameters(parameters)
        return this
    }

    override fun addParameter(name: String, value: String): ProfileRequest {
        authenticationRequest.addParameter(name, value)
        return this
    }

    /**
     * Adds a header to the request, e.g. "Authorization"
     *
     * @param name  of the header
     * @param value of the header
     * @return itself
     * @see [ProfileRequest]
     */
    override fun addHeader(name: String, value: String): ProfileRequest {
        authenticationRequest.addHeader(name, value)
        return this
    }

    /**
     * Set the scope used to authenticate the user
     *
     * @param scope value
     * @return itself
     */
    public fun setScope(scope: String): ProfileRequest {
        authenticationRequest.setScope(scope)
        return this
    }

    /**
     * Set the connection used to authenticate
     *
     * @param connection name
     * @return itself
     */
    public fun setConnection(connection: String): ProfileRequest {
        authenticationRequest.setConnection(connection)
        return this
    }

    /**
     * Starts the log in request and then fetches the user's profile
     *
     * @param callback called on either success or failure
     */
    override fun start(callback: Callback<Authentication, AuthenticationException>) {
        authenticationRequest.start(object : Callback<Credentials, AuthenticationException> {
            override fun onSuccess(credentials: Credentials) {
                userInfoRequest
                    .addHeader(HEADER_AUTHORIZATION, "Bearer " + credentials.accessToken)
                    .start(object : Callback<UserProfile, AuthenticationException> {
                        override fun onSuccess(profile: UserProfile) {
                            callback.onSuccess(Authentication(profile, credentials))
                        }

                        override fun onFailure(error: AuthenticationException) {
                            callback.onFailure(error)
                        }
                    })
            }

            override fun onFailure(error: AuthenticationException) {
                callback.onFailure(error)
            }
        })
    }

    /**
     * Logs in the user with Authok and fetches it's profile.
     *
     * @return authentication object containing the user's tokens and profile
     * @throws AuthokException when either authentication or profile fetch fails
     */
    @Throws(AuthokException::class)
    override fun execute(): Authentication {
        val credentials = authenticationRequest.execute()
        val profile = userInfoRequest
            .addHeader(HEADER_AUTHORIZATION, "Bearer " + credentials.accessToken)
            .execute()
        return Authentication(profile, credentials)
    }

    private companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
    }
}