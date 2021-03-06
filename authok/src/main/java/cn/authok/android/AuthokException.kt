package cn.authok.android

/**
 * Base Exception for any error found during a request to Authok's API
 */
public open class AuthokException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause) {

    public companion object {
        public const val UNKNOWN_ERROR: String = "a0.sdk.internal_error.unknown"
        public const val NON_JSON_ERROR: String = "a0.sdk.internal_error.plain"
        public const val EMPTY_BODY_ERROR: String = "a0.sdk.internal_error.empty"
        public const val EMPTY_RESPONSE_BODY_DESCRIPTION: String = "Empty response body"
    }
}