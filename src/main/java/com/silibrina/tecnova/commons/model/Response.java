package com.silibrina.tecnova.commons.model;

import java.util.Locale;

/**
 * This is the response of an async execution in a request (action).
 *
 * @param <T> the type of the content (payload) this response carries.
 */
public class Response<T> {
    private final T payload;

    public Response(T payload) {
        this.payload = payload;
    }

    /**
     * The content of this response, note that it can be
     * an exception.
     *
     * @return payload of this content
     */
    public T getPayload() {
        return payload;
    }

    /**
     * Test if the content is an exception.
     *
     * @return true if the content is an exception, false otherwise.
     */
    public boolean isException() {
        return payload instanceof Throwable;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "%s [payload: %s]", this.getClass().getSimpleName(), payload);
    }
}
