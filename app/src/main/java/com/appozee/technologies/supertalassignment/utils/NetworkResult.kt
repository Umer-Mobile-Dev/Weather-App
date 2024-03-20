package com.appozee.technologies.supertalassignment.utils

/**
 * A sealed class representing the result of a network operation.
 * @param data The data associated with the result, if any.
 * @param message A message describing the result, if any.
 */
sealed class NetworkResult<T>(val data: T? = null, val message: String? = null) {

    /**
     * Represents a successful network operation.
     * @param data The data associated with the success result.
     */
    class Success<T>(data: T) : NetworkResult<T>(data)

    /**
     * Represents an error that occurred during a network operation.
     * @param message A message describing the error.
     * @param data The data associated with the error result, if any.
     */
    class Error<T>(message: String?, data: T? = null) : NetworkResult<T>(data, message)

    /**
     * Represents a network operation in progress.
     */
    class Loading<T> : NetworkResult<T>()

}