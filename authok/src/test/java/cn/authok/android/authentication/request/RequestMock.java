package cn.authok.android.authentication.request;

import androidx.annotation.NonNull;

import cn.authok.android.AuthokException;
import cn.authok.android.callback.Callback;
import cn.authok.android.request.Request;

import java.util.Map;

public class RequestMock<T, U extends AuthokException> implements Request<T, U> {
    private final T result;
    private final U error;
    private boolean started;

    public RequestMock(T result, U error) {
        this.result = result;
        this.error = error;
    }

    public boolean isStarted() {
        return started;
    }

    @NonNull
    @Override
    public Request<T, U> addParameters(@NonNull Map<String, String> parameters) {
        return this;
    }

    @NonNull
    @Override
    public Request<T, U> addParameter(@NonNull String name, @NonNull String value) {
        return this;
    }

    @NonNull
    @Override
    public Request<T, U> addHeader(@NonNull String name, @NonNull String value) {
        return this;
    }

    @Override
    public void start(@NonNull Callback<T, U> callback) {
        started = true;
        if (result != null) {
            callback.onSuccess(result);
        } else {
            callback.onFailure(error);
        }
    }

    @NonNull
    @Override
    public T execute() throws AuthokException {
        return null;
    }
}
