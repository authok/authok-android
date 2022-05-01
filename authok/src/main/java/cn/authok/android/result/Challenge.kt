package cn.authok.android.result

import cn.authok.android.request.internal.JsonRequired
import com.google.gson.annotations.SerializedName

/**
 * Multi-factor authentication (MFA) challenge
 *
 * @see [cn.authok.android.authentication.AuthenticationAPIClient.multifactorChallenge]
 */
public class Challenge(
    @field:JsonRequired @field:SerializedName("challenge_type")
    public val challengeType: String,

    @field:SerializedName("oob_code")
    public val oobCode: String?,

    @field:SerializedName("binding_method")
    public val bindingMethod: String?
)