package cn.authok.android.callback

import cn.authok.android.authentication.AuthenticationException

public interface AuthenticationCallback<T> : Callback<T, AuthenticationException>