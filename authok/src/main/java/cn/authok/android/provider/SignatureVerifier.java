package cn.authok.android.provider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.authok.android.authentication.AuthenticationAPIClient;
import cn.authok.android.authentication.AuthenticationException;
import cn.authok.android.callback.AuthenticationCallback;
import cn.authok.android.callback.Callback;
import cn.authok.android.request.internal.Jwt;

import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;

/**
 * Abstract class meant to verify tokens signed with HS256 and RS256 signatures.
 */
abstract class SignatureVerifier {

    private final List<String> supportedAlgorithms;

    SignatureVerifier(List<String> supportedAlgorithms) {
        this.supportedAlgorithms = supportedAlgorithms;
    }

    /**
     * Verifies that the given token's signature is valid, deeming the payload inside it authentic
     *
     * @param token the ID token to have its signature validated
     * @throws TokenValidationException if the signature is not valid
     */
    void verify(@NonNull Jwt token) throws TokenValidationException {
        checkAlgorithm(token.getAlgorithm());
        checkSignature(token.getParts());
    }

    private void checkAlgorithm(String tokenAlgorithm) throws TokenValidationException {
        if (!supportedAlgorithms.contains(tokenAlgorithm) || "none".equalsIgnoreCase(tokenAlgorithm)) {
            if (supportedAlgorithms.size() == 1) {
                throw new TokenValidationException(String.format("Signature algorithm of \"%s\" is not supported. Expected the ID token to be signed with %s.", tokenAlgorithm, supportedAlgorithms.get(0)));
            } else {
                throw new TokenValidationException(String.format("Signature algorithm of \"%s\" is not supported. Expected the ID token to be signed with any of %s.", tokenAlgorithm, supportedAlgorithms));
            }
        }
    }

    abstract protected void checkSignature(@NonNull String[] tokenParts) throws TokenValidationException;


    /**
     * Creates a new SignatureVerifier for Asymmetric algorithm ("RS256"). Signature check will actually happen.
     *
     * @param keyId     the id of the key used to sign this token. Obtained from the token's header
     * @param apiClient the Authentication API client instance. Used to fetch the JWKs
     * @param callback  where to receive the results
     */
    static void forAsymmetricAlgorithm(@Nullable final String keyId, @NonNull AuthenticationAPIClient apiClient, @NonNull final Callback<SignatureVerifier, TokenValidationException> callback) {
        apiClient.fetchJsonWebKeys().start(new AuthenticationCallback<Map<String, PublicKey>>() {
            @Override
            public void onSuccess(@Nullable Map<String, PublicKey> result) {
                PublicKey publicKey = result.get(keyId);
                try {
                    callback.onSuccess(new AsymmetricSignatureVerifier(publicKey));
                } catch (InvalidKeyException e) {
                    callback.onFailure(new TokenValidationException(String.format("Could not find a public key for kid \"%s\"", keyId)));
                }
            }

            @Override
            public void onFailure(@NonNull AuthenticationException error) {
                callback.onFailure(new TokenValidationException(String.format("Could not find a public key for kid \"%s\"", keyId)));
            }
        });
    }
}
