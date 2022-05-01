package cn.authok.android.provider

import cn.authok.android.AuthokException

/**
 * Exception thrown when the validation of the ID token failed.
 */
internal class TokenValidationException @JvmOverloads constructor(
    message: String,
    cause: Throwable? = null
) :
    AuthokException(message, cause)