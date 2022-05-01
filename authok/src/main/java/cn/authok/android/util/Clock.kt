package cn.authok.android.util

/**
 * The clock used for verification purposes.
 *
 * @see cn.authok.android.authentication.storage.SecureCredentialsManager
 *
 * @see cn.authok.android.authentication.storage.CredentialsManager
 */
public interface Clock {
    /**
     * Returns the current time in milliseconds (epoch).
     *
     * @return the current time in milliseconds.
     */
    public fun getCurrentTimeMillis(): Long
}