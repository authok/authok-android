package cn.authok.android.util;

import androidx.annotation.NonNull;

import cn.authok.android.AuthokException;
import cn.authok.android.callback.Callback;

import java.util.concurrent.Callable;

public class MockCallback<T, U extends AuthokException> implements Callback<T, U> {

    private T payload;
    private U error;

    @Override
    public void onSuccess(@NonNull T result) {
        this.payload = result;
    }

    @Override
    public void onFailure(@NonNull U error) {
        this.error = error;
    }

    public Callable<T> payload() {
        return () -> payload;
    }

    public Callable<U> error() {
        return () -> error;
    }

    public T getPayload() {
        return payload;
    }

    public U getError() {
        return error;
    }
}
