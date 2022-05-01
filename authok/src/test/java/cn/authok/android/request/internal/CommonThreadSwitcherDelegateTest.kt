package cn.authok.android.request.internal

import cn.authok.android.AuthokException
import cn.authok.android.callback.Callback
import cn.authok.android.request.*
import cn.authok.android.util.CommonThreadSwitcherRule
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.*
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*

public class CommonThreadSwitcherDelegateTest {

    @get:Rule
    public val commonThreadSwitcherRule: TestWatcher = CommonThreadSwitcherRule()

    private lateinit var baseRequest: BaseRequest<SimplePojo, AuthokException>
    private lateinit var resultAdapter: JsonAdapter<SimplePojo>

    @Mock
    private lateinit var client: NetworkingClient

    @Mock
    private lateinit var errorAdapter: ErrorAdapter<AuthokException>

    @Mock
    private lateinit var authokException: AuthokException

    @Before
    public fun setUp() {
        MockitoAnnotations.openMocks(this)
        resultAdapter = Mockito.spy(GsonAdapter(SimplePojo::class.java, Gson()))
        baseRequest = createRequest()
    }

    private fun createRequest(): BaseRequest<SimplePojo, AuthokException> =
        BaseRequest(
            HttpMethod.POST,
            BASE_URL,
            client,
            resultAdapter,
            errorAdapter
        )

    @Test
    @Throws(Exception::class)
    public fun shouldExecuteSuccessfulRequestSynchronously() {
        val baseRequest = BaseRequest(
            HttpMethod.POST,
            BASE_URL,
            client,
            resultAdapter,
            errorAdapter
        )
        mockSuccessfulServerResponse()
        val callback: Callback<SimplePojo, AuthokException> = mock()

        baseRequest.start(callback)
        val pojoCaptor = argumentCaptor<SimplePojo>()
        verify(callback).onSuccess(pojoCaptor.capture())
        MatcherAssert.assertThat(pojoCaptor.firstValue, Matchers.`is`(Matchers.notNullValue()))
        MatcherAssert.assertThat(pojoCaptor.firstValue.prop, Matchers.`is`("test-value"))
        verify(callback, Mockito.never()).onFailure(
            any()
        )
    }

    @Test
    @Throws(Exception::class)
    public fun shouldReturnFailureSynchronously() {
        val baseRequest = BaseRequest(
            HttpMethod.POST,
            BASE_URL,
            client,
            resultAdapter,
            errorAdapter
        )
        mockFailedRawServerResponse()
        val callback: Callback<SimplePojo, AuthokException> = mock()

        baseRequest.start(callback)
        verify(callback).onFailure(
            any()
        )
        verify(callback, Mockito.never()).onSuccess(
            any()
        )
    }

    @Throws(Exception::class)
    private fun mockSuccessfulServerResponse() {
        val headers = Collections.singletonMap("Content-Type", listOf("application/json"))
        val jsonResponse = "{\"prop\":\"test-value\"}"
        val inputStream: InputStream = ByteArrayInputStream(jsonResponse.toByteArray())
        val response = ServerResponse(200, inputStream, headers)
        Mockito.`when`(
            client.load(
                eq(BASE_URL), any()
            )
        ).thenReturn(response)
    }

    @Throws(Exception::class)
    private fun mockFailedRawServerResponse() {
        val headers = Collections.singletonMap("Content-Type", listOf("text/plain"))
        val textResponse = "Failure"
        val inputStream: InputStream = ByteArrayInputStream(textResponse.toByteArray())
        Mockito.`when`(
            errorAdapter.fromRawResponse(
                eq(500),
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyMap()
            )
        ).thenReturn(authokException)
        val response = ServerResponse(500, inputStream, headers)
        Mockito.`when`(
            client.load(
                eq(BASE_URL), any()
            )
        ).thenReturn(response)
    }

    private class SimplePojo(val prop: String)
    private companion object {
        private const val BASE_URL = "https://authok.cn"
    }
}
