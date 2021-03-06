package cn.authok.android.request.internal

import cn.authok.android.AuthokException
import cn.authok.android.authentication.AuthenticationException
import cn.authok.android.authentication.ParameterBuilder
import cn.authok.android.callback.Callback
import cn.authok.android.request.AuthenticationRequest
import cn.authok.android.request.Request
import cn.authok.android.result.Credentials

internal open class BaseAuthenticationRequest(private val request: Request<Credentials, AuthenticationException>) :
    AuthenticationRequest {
    /**
     * Sets the 'grant_type' parameter
     *
     * @param grantType grant type
     * @return itself
     */
    override fun setGrantType(grantType: String): AuthenticationRequest {
        addParameter(ParameterBuilder.GRANT_TYPE_KEY, grantType)
        return this
    }

    /**
     * Sets the 'connection' parameter.
     *
     * @param connection name of the connection
     * @return itself
     */
    override fun setConnection(connection: String): AuthenticationRequest {
        addParameter(ParameterBuilder.CONNECTION_KEY, connection)
        return this
    }

    /**
     * Sets the 'realm' parameter. A realm identifies the host against which the authentication will be made, and usually helps to know which username and password to use.
     *
     * @param realm name of the realm
     * @return itself
     */
    override fun setRealm(realm: String): AuthenticationRequest {
        addParameter(ParameterBuilder.REALM_KEY, realm)
        return this
    }

    /**
     * Sets the 'scope' parameter.
     *
     * @param scope a scope value
     * @return itself
     */
    override fun setScope(scope: String): AuthenticationRequest {
        addParameter(ParameterBuilder.SCOPE_KEY, scope)
        return this
    }

    /**
     * Sets the 'audience' parameter.
     *
     * @param audience an audience value
     * @return itself
     */
    override fun setAudience(audience: String): AuthenticationRequest {
        addParameter(ParameterBuilder.AUDIENCE_KEY, audience)
        return this
    }

    override fun addParameters(parameters: Map<String, String>): AuthenticationRequest {
        request.addParameters(parameters)
        return this
    }

    override fun addParameter(name: String, value: String): AuthenticationRequest {
        request.addParameter(name, value)
        return this
    }

    override fun addHeader(name: String, value: String): AuthenticationRequest {
        request.addHeader(name, value)
        return this
    }

    override fun start(callback: Callback<Credentials, AuthenticationException>) {
        request.start(callback)
    }

    @Throws(AuthokException::class)
    override fun execute(): Credentials {
        return request.execute()
    }
}