package com.silibrina.tecnova.commons.exceptions;

/**
 * This is an exception to be throws when a malformed route is being created.
 * It might indicate a missing component of the route or a wrong part.
 */
public class MalformedRouteException extends RuntimeException {

    private static final long serialVersionUID = 3087322479474588685L;

    public MalformedRouteException() {
        super();
    }

    public MalformedRouteException(final String message) {
		super(message);
	}

    public MalformedRouteException(Throwable exception) {
        super(exception.getMessage());
        super.setStackTrace(exception.getStackTrace());
    }
}