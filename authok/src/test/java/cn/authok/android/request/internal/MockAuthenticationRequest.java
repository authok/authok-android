package cn.authok.android.request.internal;

import androidx.annotation.NonNull;

import cn.authok.android.AuthokException;
import cn.authok.android.authentication.AuthenticationException;
import cn.authok.android.callback.Callback;
import cn.authok.android.request.AuthenticationRequest;
import cn.authok.android.result.Credentials;

import java.util.HashMap;
import java.util.Map;

public class MockAuthenticationRequest extends BaseAuthenticationRequest {

    final BaseRequest<Credentials, AuthenticationException> wrappedRequest;
    final HashMap<String, String> parameters;
    final HashMap<String, String> headers;

    public MockAuthenticationRequest(BaseRequest<Credentials, AuthenticationException> request) {
        super(request);
        this.wrappedRequest = request;
        this.parameters = new HashMap<>();
        this.headers = new HashMap<>();
    }

    @NonNull
    @Override
    public AuthenticationRequest addParameters(@NonNull Map<String, String> parameters) {
        this.parameters.putAll(parameters);
        return this;
    }

    @NonNull
    @Override
    public AuthenticationRequest addParameter(@NonNull String name, @NonNull String value) {
        this.parameters.put(name, value);
        return this;
    }

    @NonNull
    @Override
    public AuthenticationRequest addHeader(@NonNull String name, @NonNull String value) {
        this.headers.put(name, value);
        return this;
    }

    @Override
    public void start(@NonNull Callback<Credentials, AuthenticationException> callback) {
    }

    @NonNull
    @Override
    public Credentials execute() throws AuthokException {
        return null;
    }
}
