package cn.authok.android

/**
 * Exception that represents a failure caused when attempting to execute a network request
 */
public class NetworkErrorException(cause: Throwable) :
    AuthokException("Failed to execute the network request", cause)