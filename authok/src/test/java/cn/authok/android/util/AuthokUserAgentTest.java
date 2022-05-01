package cn.authok.android.util;

import android.util.Base64;

import cn.authok.android.authok.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(RobolectricTestRunner.class)
public class AuthokUserAgentTest {

    //Testing Android version only for a few SDKs

    @Test
    @Config(sdk = 21)
    public void shouldAlwaysIncludeAndroidVersionAPI21() {
        AuthokUserAgent authokUserAgent = new AuthokUserAgent("authok-java", "1.2.3");
        assertThat(authokUserAgent.getEnvironment(), is(notNullValue()));
        assertThat(authokUserAgent.getEnvironment().get("android"), is("21"));
    }

    @Test
    @Config(sdk = 23)
    public void shouldAlwaysIncludeAndroidVersionAPI23() {
        AuthokUserAgent authokUserAgent = new AuthokUserAgent("authok-java", "1.2.3");
        assertThat(authokUserAgent.getEnvironment(), is(notNullValue()));
        assertThat(authokUserAgent.getEnvironment().get("android"), is("23"));
    }

    @Test
    public void shouldUseDefaultValuesWithDefaultConstructor() {
        AuthokUserAgent authokUserAgent = new AuthokUserAgent();
        assertThat(authokUserAgent.getValue(), is(notNullValue()));
        assertThat(authokUserAgent.getName(), is(BuildConfig.LIBRARY_NAME));
        assertThat(authokUserAgent.getVersion(), is(BuildConfig.VERSION_NAME));
        assertThat(authokUserAgent.getEnvironment(), is(notNullValue()));
        assertThat(authokUserAgent.getLibraryVersion(), is(nullValue()));
    }

    @Test
    public void shouldUseDefaultNameEmpty() {
        AuthokUserAgent authokUserAgent = new AuthokUserAgent("", "2.0");
        assertThat(authokUserAgent.getValue(), is(notNullValue()));
        assertThat(authokUserAgent.getName(), is(BuildConfig.LIBRARY_NAME));
        assertThat(authokUserAgent.getVersion(), is("2.0"));
        assertThat(authokUserAgent.getEnvironment(), is(notNullValue()));
        assertThat(authokUserAgent.getLibraryVersion(), is(nullValue()));
    }

    @Test
    public void shouldUseDefaultVersionIfEmpty() {
        AuthokUserAgent authokUserAgent = new AuthokUserAgent("authok-java", "");
        assertThat(authokUserAgent.getValue(), is(notNullValue()));
        assertThat(authokUserAgent.getName(), is("authok-java"));
        assertThat(authokUserAgent.getVersion(), is(BuildConfig.VERSION_NAME));
        assertThat(authokUserAgent.getEnvironment(), is(notNullValue()));
        assertThat(authokUserAgent.getLibraryVersion(), is(nullValue()));
    }

    @Test
    public void shouldNotIncludeLibraryVersionIfNotProvided() {
        AuthokUserAgent authokUserAgent = new AuthokUserAgent();
        assertThat(authokUserAgent.getEnvironment(), is(notNullValue()));
        assertThat(authokUserAgent.getEnvironment().containsKey("authok.android"), is(false));
    }

    @Test
    public void shouldGetName() {
        AuthokUserAgent authokUserAgent = new AuthokUserAgent("authok-java", "1.0.0", "1.2.3");
        assertThat(authokUserAgent.getName(), is("authok-java"));
    }

    @Test
    public void shouldGetVersion() {
        AuthokUserAgent authokUserAgent = new AuthokUserAgent("authok-java", "1.0.0", "1.2.3");
        assertThat(authokUserAgent.getVersion(), is("1.0.0"));
    }

    @Test
    public void shouldGetLibraryVersion() {
        AuthokUserAgent authokUserAgent = new AuthokUserAgent("authok-java", "1.0.0", "1.2.3");
        assertThat(authokUserAgent.getLibraryVersion(), is("1.2.3"));
        assertThat(authokUserAgent.getEnvironment().get("authok.android"), is("1.2.3"));
    }

    @Test
    @Config(sdk = 23)
    public void shouldGenerateCompleteTelemetryBase64Value() {
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();

        AuthokUserAgent authokUserAgentComplete = new AuthokUserAgent("authok-java", "1.0.0", "1.2.3");
        String value = authokUserAgentComplete.getValue();
        assertThat(value, is("eyJuYW1lIjoiYXV0aDAtamF2YSIsImVudiI6eyJhbmRyb2lkIjoiMjMiLCJhdXRoMC5hbmRyb2lkIjoiMS4yLjMifSwidmVyc2lvbiI6IjEuMC4wIn0="));
        String completeString = new String(Base64.decode(value, Base64.URL_SAFE | Base64.NO_WRAP), StandardCharsets.UTF_8);
        Map<String, Object> complete = gson.fromJson(completeString, mapType);
        assertThat((String) complete.get("name"), is("authok-java"));
        assertThat((String) complete.get("version"), is("1.0.0"));
        Map<String, Object> completeEnv = (Map<String, Object>) complete.get("env");
        assertThat((String) completeEnv.get("authok.android"), is("1.2.3"));
        assertThat((String) completeEnv.get("android"), is("23"));
    }

    @Test
    @Config(sdk = 23)
    public void shouldGenerateBasicTelemetryBase64Value() {
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();

        AuthokUserAgent authokUserAgentBasic = new AuthokUserAgent("authok-python", "99.3.1");
        String value = authokUserAgentBasic.getValue();
        assertThat(value, is("eyJuYW1lIjoiYXV0aDAtcHl0aG9uIiwiZW52Ijp7ImFuZHJvaWQiOiIyMyJ9LCJ2ZXJzaW9uIjoiOTkuMy4xIn0="));
        String basicString = new String(Base64.decode(value, Base64.URL_SAFE | Base64.NO_WRAP), StandardCharsets.UTF_8);
        Map<String, Object> basic = gson.fromJson(basicString, mapType);
        assertThat((String) basic.get("name"), is("authok-python"));
        assertThat((String) basic.get("version"), is("99.3.1"));
        Map<String, Object> basicEnv = (Map<String, Object>) basic.get("env");
        assertThat(basicEnv.get("authok.android"), is(nullValue()));
        assertThat((String) basicEnv.get("android"), is("23"));
    }
}