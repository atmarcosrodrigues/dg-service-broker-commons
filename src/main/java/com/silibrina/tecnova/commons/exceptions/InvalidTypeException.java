package com.silibrina.tecnova.commons.exceptions;

/**
 * This is an exception to be thrown when an invalid type is given.
 */
public class InvalidTypeException extends RuntimeException {

    /**
     * Constructor of an invalid type exception with the given message.
     * @param message the message this exception must carry.
     */
    public InvalidTypeException(String message) {
        super(message);
    }
}
