package cn.authok.android.request.internal;

import androidx.annotation.NonNull;

import cn.authok.android.AuthokException;
import cn.authok.android.callback.Callback;
import cn.authok.android.request.Request;

import java.util.HashMap;
import java.util.Map;

public class MockRequest<T, U extends AuthokException> implements Request<T, U> {

    public final Map<String, String> parameters = new HashMap<>();
    public final Map<String, String> headers = new HashMap<>();

    @NonNull
    @Override
    public Request<T, U> addParameters(@NonNull Map<String, String> parameters) {
        this.parameters.putAll(parameters);
        return this;
    }

    @NonNull
    @Override
    public Request<T, U> addParameter(@NonNull String name, @NonNull String value) {
        this.parameters.put(name, value);
        return this;
    }

    @NonNull
    @Override
    public Request<T, U> addHeader(@NonNull String name, @NonNull String value) {
        this.headers.put(name, value);
        return this;
    }

    @Override
    public void start(@NonNull Callback<T, U> callback) {
    }

    @NonNull
    @Override
    public T execute() throws AuthokException {
        return null;
    }
}
