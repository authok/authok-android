package cn.authok.android.request.internal

import cn.authok.android.result.Credentials
import cn.authok.android.result.CredentialsMock
import java.util.*

internal class CredentialsDeserializerMock : CredentialsDeserializer() {
    override fun createCredentials(
        idToken: String,
        accessToken: String,
        type: String,
        refreshToken: String?,
        expiresAt: Date,
        scope: String?,
        recoveryCode: String?
    ): Credentials {
        val credentials =
            CredentialsMock(idToken, accessToken, type, refreshToken, expiresAt, scope)
        credentials.recoveryCode = recoveryCode
        return credentials
    }

    override val currentTimeInMillis: Long
        get() = CredentialsMock.CURRENT_TIME_MS
}