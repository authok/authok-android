package cn.authok.android.authentication.storage

import cn.authok.android.AuthokException

/**
 * Represents an error raised by the [CredentialsManager].
 */
public class CredentialsManagerException internal constructor(
    message: String,
    cause: Throwable? = null
) : AuthokException(message, cause) {

    /**
     * Returns true when this Android device doesn't support the cryptographic algorithms used
     * to handle encryption and decryption, false otherwise.
     *
     * @return whether this device is compatible with [SecureCredentialsManager] or not.
     */
    public val isDeviceIncompatible: Boolean
        get() = cause is IncompatibleDeviceException
}