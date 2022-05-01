package cn.authok.android.result

/**
 * The result of a successful authentication against Authok
 * Contains the logged in user's [Credentials] and [UserProfile].
 *
 * @see [cn.authok.android.authentication.AuthenticationAPIClient.getProfileAfter]
 */
public class Authentication(public val profile: UserProfile, public val credentials: Credentials)