package cn.authok.android.request.internal;


import cn.authok.android.AuthokException;
import cn.authok.android.request.ErrorAdapter;
import cn.authok.android.request.HttpMethod;
import cn.authok.android.request.JsonAdapter;
import cn.authok.android.request.NetworkingClient;
import cn.authok.android.request.Request;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class RequestFactoryTest {

    private static final String CLIENT_INFO = "client_info";
    private static final String BASE_URL = "http://domain.authok.cn";

    @Mock
    private NetworkingClient client;
    @Mock
    private ErrorAdapter<AuthokException> errorAdapter;
    @Mock
    private JsonAdapter<String> resultAdapter;
    @Mock
    private Request<String, AuthokException> postRequest;
    @Mock
    private Request<String, AuthokException> emptyPostRequest;
    @Mock
    private Request<String, AuthokException> patchRequest;
    @Mock
    private Request<String, AuthokException> getRequest;
    @Mock
    private Request<String, AuthokException> deleteRequest;

    private RequestFactory<AuthokException> factory;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        factory = createRequestFactory();
    }

    @Test
    public void shouldHaveDefaultAcceptLanguageHeader() {
        final Locale locale = new Locale("");
        Locale.setDefault(locale);
        // recreate the factory to read the default again
        RequestFactory<AuthokException> factory = createRequestFactory();

        factory.get(BASE_URL, resultAdapter);
        verify(getRequest).addHeader("Accept-Language", "en_US");

        factory.post(BASE_URL, resultAdapter);
        verify(postRequest).addHeader("Accept-Language", "en_US");

        factory.delete(BASE_URL, resultAdapter);
        verify(deleteRequest).addHeader("Accept-Language", "en_US");

        factory.patch(BASE_URL, resultAdapter);
        verify(patchRequest).addHeader("Accept-Language", "en_US");
    }

    @Test
    public void shouldHaveAcceptLanguageHeader() {
        final Locale localeJP = new Locale("ja", "JP");
        Locale.setDefault(localeJP);
        // recreate the factory to read the default again
        RequestFactory<AuthokException> factory = createRequestFactory();

        factory.get(BASE_URL, resultAdapter);
        verify(getRequest).addHeader("Accept-Language", "ja_JP");

        factory.post(BASE_URL, resultAdapter);
        verify(postRequest).addHeader("Accept-Language", "ja_JP");

        factory.delete(BASE_URL, resultAdapter);
        verify(deleteRequest).addHeader("Accept-Language", "ja_JP");

        factory.patch(BASE_URL, resultAdapter);
        verify(patchRequest).addHeader("Accept-Language", "ja_JP");
    }

    @Test
    public void shouldHaveCustomHeader() {
        RequestFactory<AuthokException> factory = createRequestFactory();
        factory.setHeader("the-header", "the-value");

        factory.get(BASE_URL, resultAdapter);
        verify(getRequest).addHeader("the-header", "the-value");

        factory.post(BASE_URL, resultAdapter);
        verify(postRequest).addHeader("the-header", "the-value");

        factory.delete(BASE_URL, resultAdapter);
        verify(deleteRequest).addHeader("the-header", "the-value");

        factory.patch(BASE_URL, resultAdapter);
        verify(patchRequest).addHeader("the-header", "the-value");
    }

    @Test
    public void shouldHaveClientInfoHeader() {
        RequestFactory<AuthokException> factory = createRequestFactory();
        factory.setAuthokClientInfo(CLIENT_INFO);

        factory.get(BASE_URL, resultAdapter);
        verify(getRequest).addHeader("Authok-Client", CLIENT_INFO);

        factory.post(BASE_URL, resultAdapter);
        verify(postRequest).addHeader("Authok-Client", CLIENT_INFO);

        factory.delete(BASE_URL, resultAdapter);
        verify(deleteRequest).addHeader("Authok-Client", CLIENT_INFO);

        factory.patch(BASE_URL, resultAdapter);
        verify(patchRequest).addHeader("Authok-Client", CLIENT_INFO);
    }

    @Test
    public void shouldCreatePostRequest() {
        Request<String, AuthokException> request = factory.post(BASE_URL, resultAdapter);

        assertThat(request, is(notNullValue()));
        assertThat(request, is(postRequest));
    }

    @Test
    public void shouldCreateVoidPostRequest() {
        Request<Void, AuthokException> request = factory.post(BASE_URL);

        assertThat(request, is(notNullValue()));
        assertThat(request, is(emptyPostRequest));
    }

    @Test
    public void shouldCreatePatchRequest() {
        Request<String, AuthokException> request = factory.patch(BASE_URL, resultAdapter);

        assertThat(request, is(notNullValue()));
        assertThat(request, is(patchRequest));
    }

    @Test
    public void shouldCreateDeleteRequest() {
        Request<String, AuthokException> request = factory.delete(BASE_URL, resultAdapter);

        assertThat(request, is(notNullValue()));
        assertThat(request, is(deleteRequest));
    }

    @Test
    public void shouldCreateGetRequest() {
        Request<String, AuthokException> request = factory.get(BASE_URL, resultAdapter);

        assertThat(request, is(notNullValue()));
        assertThat(request, is(getRequest));
    }

    @SuppressWarnings("unchecked")
    private RequestFactory<AuthokException> createRequestFactory() {
        RequestFactory<AuthokException> factory = spy(new RequestFactory<>(client, errorAdapter));
        doReturn(postRequest).when(factory).createRequest(any(HttpMethod.POST.class), eq(BASE_URL), eq(client), eq(resultAdapter), eq(errorAdapter), any(ThreadSwitcher.class));
        doReturn(emptyPostRequest).when(factory).createRequest(any(HttpMethod.POST.class), eq(BASE_URL), eq(client), AdditionalMatchers.and(AdditionalMatchers.not(ArgumentMatchers.eq(resultAdapter)), ArgumentMatchers.isA(JsonAdapter.class)), eq(errorAdapter), any(ThreadSwitcher.class));
        doReturn(deleteRequest).when(factory).createRequest(any(HttpMethod.DELETE.class), eq(BASE_URL), eq(client), eq(resultAdapter), eq(errorAdapter), any(ThreadSwitcher.class));
        doReturn(patchRequest).when(factory).createRequest(any(HttpMethod.PATCH.class), eq(BASE_URL), eq(client), eq(resultAdapter), eq(errorAdapter), any(ThreadSwitcher.class));
        doReturn(getRequest).when(factory).createRequest(any(HttpMethod.GET.class), eq(BASE_URL), eq(client), eq(resultAdapter), eq(errorAdapter), any(ThreadSwitcher.class));
        return factory;
    }
}