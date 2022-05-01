package cn.authok.android;

import android.content.Context;
import android.content.res.Resources;

import cn.authok.android.util.AuthokUserAgent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import okhttp3.HttpUrl;

import static cn.authok.android.util.HttpUrlMatcher.hasHost;
import static cn.authok.android.util.HttpUrlMatcher.hasPath;
import static cn.authok.android.util.HttpUrlMatcher.hasScheme;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class AuthokTest {

    @Mock
    public Context context;

    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String PACKAGE_NAME = "com.sample.app";
    private static final String DOMAIN = "samples.cn.authok.cn";
    private static final String CONFIG_DOMAIN_CUSTOM = "config.mydomain.com";
    private static final String OTHER_DOMAIN = "samples-test.other-subdomain.other.authok.cn";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(context.getPackageName()).thenReturn(PACKAGE_NAME);
        when(context.getString(eq(222))).thenReturn(CLIENT_ID);
        when(context.getString(eq(333))).thenReturn(DOMAIN);
    }

    @Test
    public void shouldBuildFromResources() {
        Resources resources = Mockito.mock(Resources.class);
        when(context.getResources()).thenReturn(resources);
        when(resources.getIdentifier(eq("cn_authok_client_id"), eq("string"), eq(PACKAGE_NAME))).thenReturn(222);
        when(resources.getIdentifier(eq("cn_authok_domain"), eq("string"), eq(PACKAGE_NAME))).thenReturn(333);

        when(context.getString(eq(222))).thenReturn(CLIENT_ID);
        when(context.getString(eq(333))).thenReturn(DOMAIN);

        Authok authok = new Authok(context);

        assertThat(authok, notNullValue());
        assertThat(authok.getClientId(), equalTo(CLIENT_ID));
        assertThat(authok.getDomainUrl(), equalTo("https://samples.cn.authok.cn/"));
        assertThat(authok.getConfigurationUrl(), equalTo("https://samples.cn.authok.cn/"));
    }

    @Test
    public void shouldFailToBuildFromResourcesWithoutClientID() {
        Assert.assertThrows("The 'R.string.cn_authok_client_id' value it's not defined in your project's resources file.", IllegalArgumentException.class, () -> {
            Resources resources = Mockito.mock(Resources.class);
            when(context.getResources()).thenReturn(resources);
            when(resources.getIdentifier(eq("cn_authok_client_id"), eq("string"), eq(PACKAGE_NAME))).thenReturn(0);
            when(resources.getIdentifier(eq("cn_authok_domain"), eq("string"), eq(PACKAGE_NAME))).thenReturn(333);

            new Authok(context);
        });
    }

    @Test
    public void shouldFailToBuildFromResourcesWithoutDomain() {
        Assert.assertThrows("The 'R.string.cn_authok_domain' value it's not defined in your project's resources file.", IllegalArgumentException.class, () -> {
            Resources resources = Mockito.mock(Resources.class);
            when(context.getResources()).thenReturn(resources);
            when(resources.getIdentifier(eq("cn_authok_client_id"), eq("string"), eq(PACKAGE_NAME))).thenReturn(222);
            when(resources.getIdentifier(eq("cn_authok_domain"), eq("string"), eq(PACKAGE_NAME))).thenReturn(0);

            new Authok(context);
        });
    }

    @Test
    public void shouldBuildWithClientIdAndDomain() {
        Authok authok = new Authok(CLIENT_ID, DOMAIN);
        assertThat(authok.getClientId(), equalTo(CLIENT_ID));
        assertThat(HttpUrl.parse(authok.getDomainUrl()), equalTo(HttpUrl.parse("https://samples.cn.authok.cn")));
        assertThat(HttpUrl.parse(authok.getConfigurationUrl()), equalTo(HttpUrl.parse("https://samples.cn.authok.cn")));
    }

    @Test
    public void shouldBuildWithConfigurationDomainToo() {
        Authok authok = new Authok(CLIENT_ID, DOMAIN, CONFIG_DOMAIN_CUSTOM);
        assertThat(authok.getClientId(), equalTo(CLIENT_ID));
        assertThat(HttpUrl.parse(authok.getDomainUrl()), equalTo(HttpUrl.parse("https://samples.cn.authok.cn")));
        assertThat(HttpUrl.parse(authok.getConfigurationUrl()), equalTo(HttpUrl.parse("https://config.mydomain.com")));
    }

    @Test
    public void shouldHandleOtherInstance() {
        Authok authok = new Authok(CLIENT_ID, OTHER_DOMAIN);
        assertThat(authok.getClientId(), equalTo(CLIENT_ID));
        assertThat(HttpUrl.parse(authok.getDomainUrl()), equalTo(HttpUrl.parse("https://samples-test.other-subdomain.other.authok.cn")));
        assertThat(HttpUrl.parse(authok.getConfigurationUrl()), equalTo(HttpUrl.parse("https://samples-test.other-subdomain.other.authok.cn")));
    }

    @Test
    public void shouldHandleNonAuthokDomain() {
        Authok authok = new Authok(CLIENT_ID, "mydomain.com");
        assertThat(authok.getClientId(), equalTo(CLIENT_ID));
        assertThat(HttpUrl.parse(authok.getDomainUrl()), equalTo(HttpUrl.parse("https://mydomain.com")));
        assertThat(HttpUrl.parse(authok.getConfigurationUrl()), equalTo(HttpUrl.parse("https://mydomain.com")));
    }

    @Test
    public void shouldThrowWhenInvalidDomain() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new Authok(CLIENT_ID, "some invalid domain.com"));
    }

    @Test
    public void shouldReturnAuthorizeUrl() {
        Authok authok = new Authok(CLIENT_ID, DOMAIN);

        final HttpUrl url = HttpUrl.parse(authok.getAuthorizeUrl());
        assertThat(url, hasScheme("https"));
        assertThat(url, hasHost(DOMAIN));
        assertThat(url, hasPath("authorize"));
    }

    @Test
    public void shouldReturnLogoutUrl() {
        Authok authok = new Authok(CLIENT_ID, DOMAIN);

        final HttpUrl url = HttpUrl.parse(authok.getLogoutUrl());
        assertThat(url, hasScheme("https"));
        assertThat(url, hasHost(DOMAIN));
        assertThat(url, hasPath("v1", "logout"));
    }

    @Test
    public void shouldSetCustomTelemetry() {
        AuthokUserAgent customAuthokUserAgent = new AuthokUserAgent("custom", "9.9.9", "1.1.1");
        Authok authok = new Authok(CLIENT_ID, DOMAIN);
        authok.setAuthokUserAgent(customAuthokUserAgent);
        assertThat(authok.getAuthokUserAgent(), is(equalTo(customAuthokUserAgent)));
    }

    @Test
    public void shouldThrowWhenHttpDomainUsed() {
        Assert.assertThrows("Invalid domain url: 'http://" + DOMAIN + "'. Only HTTPS domain URLs are supported. If no scheme is passed, HTTPS will be used.", IllegalArgumentException.class, () -> new Authok(CLIENT_ID, "http://" + DOMAIN));
    }

    @Test
    public void shouldHandleUpperCaseHttpsDomain() {
        Authok authok = new Authok(CLIENT_ID, "Https://" + DOMAIN);
        assertThat(authok.getDomainUrl(), is("https://" + DOMAIN + "/"));
    }

    @Test
    public void shouldThrowWhenHttpUppercaseDomainUsed() {
        Assert.assertThrows("Invalid domain url: 'http://" + DOMAIN + "'. Only HTTPS domain URLs are supported. If no scheme is passed, HTTPS will be used.", IllegalArgumentException.class, () -> new Authok(CLIENT_ID, "HTTP://" + DOMAIN));
    }

    @Test
    public void shouldThrowWhenConfigDomainIsHttp() {
        Assert.assertThrows("Invalid domain url: 'http://" + OTHER_DOMAIN + "'. Only HTTPS domain URLs are supported. If no scheme is passed, HTTPS will be used.", IllegalArgumentException.class, () -> new Authok(CLIENT_ID, DOMAIN, "HTTP://" + OTHER_DOMAIN));
    }
}