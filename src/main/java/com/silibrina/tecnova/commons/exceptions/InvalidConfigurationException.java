package com.silibrina.tecnova.commons.exceptions;

/**
 * This is an exception to be thrown when an invalid condition happens.
 */
public class InvalidConfigurationException extends RuntimeException {
    private static final long serialVersionUID = 7462284321422877091L;

    /**
     * Constructor of a invalid condition exception without message.
     */
    public InvalidConfigurationException() {
        super();
	}

    /**
     * Constructor of a invalid condition exception with the given message.
     * @param message a message to be show by the JVM or given as information
     *                to the api.
     */
    public InvalidConfigurationException(String message) {
		super(message);
	}
}
