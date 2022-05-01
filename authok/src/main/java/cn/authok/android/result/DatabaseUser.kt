package cn.authok.android.result

import cn.authok.android.request.internal.JsonRequired
import com.google.gson.annotations.SerializedName

/**
 * Authok user created in a Database connection.
 *
 * @see [cn.authok.android.authentication.AuthenticationAPIClient.signUp]
 */
public class DatabaseUser(
    @field:JsonRequired @field:SerializedName("email") public val email: String,
    @field:SerializedName(
        "username"
    ) public val username: String?,
    @field:SerializedName("email_verified") public val isEmailVerified: Boolean
)