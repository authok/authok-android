package cn.authok.android.request.internal

import cn.authok.android.result.UserIdentity
import cn.authok.android.util.UserIdentityMatcher
import com.google.gson.JsonParseException
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.collection.IsMapWithSize
import org.junit.Assert
import org.junit.Before
import org.junit.Test

public class UserIdentityGsonTest : GsonBaseTest() {

    @Before
    public fun setUp() {
        gson = GsonProvider.gson
    }

    @Test
    @Throws(Exception::class)
    public fun shouldFailWithInvalidJson() {
        Assert.assertThrows(JsonParseException::class.java) {
            pojoFrom(json(INVALID), UserIdentity::class.java)
        }
    }

    @Test
    @Throws(Exception::class)
    public fun shouldFailWithEmptyJson() {
        Assert.assertThrows(JsonParseException::class.java) {
            pojoFrom(json(EMPTY_OBJECT), UserIdentity::class.java)
        }
    }

    @Test
    @Throws(Exception::class)
    public fun shouldBuildBasic() {
        val identity = pojoFrom(json(AUTHOK), UserIdentity::class.java)
        MatcherAssert.assertThat(
            identity,
            UserIdentityMatcher.isUserIdentity(
                "1234567890",
                "authok",
                "Username-Password-Authentication"
            )
        )
        MatcherAssert.assertThat(identity.getProfileInfo(), IsMapWithSize.anEmptyMap())
        MatcherAssert.assertThat(identity.isSocial, Matchers.`is`(false))
        MatcherAssert.assertThat(identity.accessToken, Matchers.nullValue())
        MatcherAssert.assertThat(identity.accessTokenSecret, Matchers.nullValue())
    }

    @Test
    @Throws(Exception::class)
    public fun shouldBuildWithExtraValues() {
        val identity = pojoFrom(json(FACEBOOK), UserIdentity::class.java)
        MatcherAssert.assertThat(
            identity,
            UserIdentityMatcher.isUserIdentity("999997950999976", "facebook", "facebook")
        )
        MatcherAssert.assertThat(
            identity.getProfileInfo(),
            Matchers.hasEntry("given_name", "John" as Any)
        )
        MatcherAssert.assertThat(
            identity.getProfileInfo(),
            Matchers.hasEntry("family_name", "Foobar" as Any)
        )
        MatcherAssert.assertThat(
            identity.getProfileInfo(),
            Matchers.hasEntry("email_verified", true as Any)
        )
        MatcherAssert.assertThat(
            identity.getProfileInfo(),
            Matchers.hasEntry("gender", "male" as Any)
        )
    }

    private companion object {
        private const val AUTHOK = "src/test/resources/identity_authok.json"
        private const val FACEBOOK = "src/test/resources/identity_facebook.json"
    }
}