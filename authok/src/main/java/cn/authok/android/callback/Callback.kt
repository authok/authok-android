package cn.authok.android.callback

import cn.authok.android.AuthokException

/**
 * Interface for all callbacks used with Authok API clients
 */
public interface Callback<T, U : AuthokException> {

    /**
     * Method called on success with the result.
     *
     * @param result Request result
     */
    public fun onSuccess(result: T)

    /**
     * Method called on Authok API request failure
     *
     * @param error The reason of the failure
     */
    public fun onFailure(error: U)
}