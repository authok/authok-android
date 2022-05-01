package cn.authok.android.callback

import cn.authok.android.AuthokException

/**
 * Legacy interface to handle successful callbacks. Use {@linkplain Callback} instead.
 */
@Deprecated(
    message = "The contract of this interface has been migrated to the Callback interface",
    replaceWith = ReplaceWith("Callback")
)
public interface BaseCallback<T, U : AuthokException> : Callback<T, U>