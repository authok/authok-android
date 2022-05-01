package cn.authok.android.authentication.storage;

import cn.authok.android.request.internal.Jwt;

/**
 * Bridge class for decoding JWTs.
 * Used to abstract the implementation for testing purposes.
 */
class JWTDecoder {

    JWTDecoder() {
    }

    Jwt decode(String jwt) {
        return new Jwt(jwt);
    }
}
