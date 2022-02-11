package com.silibrina.tecnova.commons.exceptions;

import com.silibrina.tecnova.commons.messenger.MessageStatus;

/**
 * This is an exception to be thrown when an invalid condition happens.
 */
public class InvalidConditionException extends RuntimeException {
    private static final long serialVersionUID = 7462284321422877091L;

    private final MessageStatus status;

    public InvalidConditionException(MessageStatus status) {
        super();

        this.status = status;
    }

    /**
     * Constructor of a invalid condition exception without message.
     *
     * @param message The message of this exception.
     */
    public InvalidConditionException(String message) {
        super(message);

        status = MessageStatus.UNDEFINED_STATUS;
	}

    /**
     * Constructor of a invalid condition exception with the given message.
     *
     * @param message a message to be show by the JVM or given as information
     *                to the api.
     * @param status The status this exception must carry.
     */
    public InvalidConditionException(String message, MessageStatus status) {
		super(message);

        this.status = status;
	}

	public MessageStatus getStatus() {
        return status;
    }
}
