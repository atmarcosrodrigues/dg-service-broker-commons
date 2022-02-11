package com.silibrina.tecnova.commons.exceptions;

public class RouteNotFoundException extends RuntimeException {

    public RouteNotFoundException() {
        super();
    }

    public RouteNotFoundException(final String message) {
        super(message);
    }
}
