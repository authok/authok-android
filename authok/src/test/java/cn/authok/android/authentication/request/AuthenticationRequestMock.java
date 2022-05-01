package cn.authok.android.authentication.request;

import androidx.annotation.NonNull;

import cn.authok.android.AuthokException;
import cn.authok.android.authentication.AuthenticationException;
import cn.authok.android.callback.Callback;
import cn.authok.android.request.AuthenticationRequest;
import cn.authok.android.result.Credentials;

import java.util.Map;

public class AuthenticationRequestMock implements AuthenticationRequest {
    private final Credentials credentials;
    private final AuthenticationException error;
    private boolean started;

    public AuthenticationRequestMock(Credentials credentials, AuthenticationException error) {
        this.credentials = credentials;
        this.error = error;
    }

    @Override
    public void start(@NonNull Callback<Credentials, AuthenticationException> callback) {
        started = true;
        if (credentials != null) {
            callback.onSuccess(credentials);
        } else {
            callback.onFailure(error);
        }
    }

    @NonNull
    @Override
    public Credentials execute() throws AuthokException {
        return credentials;
    }

    @NonNull
    @Override
    public AuthenticationRequest addParameters(@NonNull Map<String, String> parameters) {
        return this;
    }

    @NonNull
    @Override
    public AuthenticationRequest addParameter(@NonNull String name, @NonNull String value) {
        return this;
    }

    @NonNull
    @Override
    public AuthenticationRequest addHeader(@NonNull String name, @NonNull String value) {
        return this;
    }

    @NonNull
    @Override
    public AuthenticationRequest setGrantType(@NonNull String grantType) {
        return this;
    }

    @NonNull
    @Override
    public AuthenticationRequest setConnection(@NonNull String connection) {
        return this;
    }

    @NonNull
    @Override
    public AuthenticationRequest setRealm(@NonNull String realm) {
        return this;
    }

    @NonNull
    @Override
    public AuthenticationRequest setScope(@NonNull String scope) {
        return this;
    }

    @NonNull
    @Override
    public AuthenticationRequest setAudience(@NonNull String audience) {
        return this;
    }

    public boolean isStarted() {
        return started;
    }

}
