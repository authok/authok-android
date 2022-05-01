package cn.authok.android.util;

import android.app.Dialog;

import androidx.annotation.NonNull;

import cn.authok.android.authentication.AuthenticationException;
import cn.authok.android.provider.AuthCallback;
import cn.authok.android.result.Credentials;

import java.util.concurrent.Callable;

public class MockAuthCallback implements AuthCallback {

    private AuthenticationException error;
    private Credentials credentials;
    private Dialog dialog;


    public Callable<AuthenticationException> error() {
        return () -> error;
    }

    public Callable<Dialog> dialog() {
        return () -> dialog;
    }

    public Callable<Credentials> credentials() {
        return () -> credentials;
    }

    public AuthenticationException getError() {
        return error;
    }

    public Dialog getErrorDialog() {
        //unused
        return dialog;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    @Override
    public void onFailure(@NonNull Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onFailure(@NonNull AuthenticationException exception) {
        this.error = exception;
    }

    @Override
    public void onSuccess(@NonNull Credentials credentials) {
        this.credentials = credentials;
    }
}
