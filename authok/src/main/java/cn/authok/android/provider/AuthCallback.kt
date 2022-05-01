package cn.authok.android.provider

import android.app.Dialog
import cn.authok.android.authentication.AuthenticationException
import cn.authok.android.result.Credentials

/**
 * Callback called on success/failure of an Identity Provider authentication.
 * Only one of the success or failure methods will be called for a single authentication request.
 */
public interface AuthCallback {
    /**
     * Called when the failure reason is displayed in a [android.app.Dialog].
     *
     * @param dialog error dialog
     */
    public fun onFailure(dialog: Dialog)

    /**
     * Called with an AuthenticationException that describes the error.
     *
     * @param exception cause of the error
     */
    public fun onFailure(exception: AuthenticationException)

    /**
     * Called when the authentication is successful using web authentication against Authok
     *
     * @param credentials Authok credentials information (id_token, refresh_token, etc).
     */
    public fun onSuccess(credentials: Credentials)
}