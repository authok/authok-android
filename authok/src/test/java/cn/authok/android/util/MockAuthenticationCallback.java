package cn.authok.android.util;

import androidx.annotation.NonNull;

import cn.authok.android.authentication.AuthenticationException;
import cn.authok.android.callback.AuthenticationCallback;

import java.util.concurrent.Callable;

public class MockAuthenticationCallback<T> implements AuthenticationCallback<T> {

    private AuthenticationException error;
    private T payload;

    @Override
    public void onFailure(@NonNull AuthenticationException error) {
        this.error = error;
    }

    @Override
    public void onSuccess(@NonNull T result) {
        this.payload = result;
    }

    public Callable<AuthenticationException> error() {
        return () -> error;
    }

    public Callable<T> payload() {
        return () -> payload;
    }

    public AuthenticationException getError() {
        return error;
    }

    public T getPayload() {
        return payload;
    }
}
