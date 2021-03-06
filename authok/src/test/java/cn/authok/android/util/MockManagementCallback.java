package cn.authok.android.util;

import androidx.annotation.NonNull;

import cn.authok.android.callback.ManagementCallback;
import cn.authok.android.management.ManagementException;

import java.util.concurrent.Callable;

public class MockManagementCallback<T> implements ManagementCallback<T> {

    private ManagementException error;
    private T payload;

    @Override
    public void onFailure(@NonNull ManagementException error) {
        this.error = error;
    }

    @Override
    public void onSuccess(@NonNull T result) {
        this.payload = result;
    }

    public Callable<ManagementException> error() {
        return () -> error;
    }

    public Callable<T> payload() {
        return () -> payload;
    }

    public ManagementException getError() {
        return error;
    }

    public T getPayload() {
        return payload;
    }
}
