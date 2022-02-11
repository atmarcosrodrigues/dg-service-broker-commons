package com.silibrina.tecnova.commons.exceptions;

/**
 * This is an exception to be thrown when an unrecoverable exception happens and
 * the system must halt.
 */
public class UnrecoverableErrorException extends RuntimeException {
    private static final long serialVersionUID = -2996500574080223486L;
    private final ExitStatus exitStatus;

    /**
     * Constructor of an unrecoverable internal error exception without message with status.
     * @param exitStatus An integer between 0 - 255, where 0 means OK.
     */
    public UnrecoverableErrorException(final ExitStatus exitStatus) {
        super();

        this.exitStatus = exitStatus;
    }

    /**
     * Constructor of an unrecoverable internal error with the given message and status.
     * @param message A message describing the error.
     * @param exitStatus An integer between 0 - 255, where 0 means OK.
     */
    public UnrecoverableErrorException(final String message, final ExitStatus exitStatus) {
		super(message);

        this.exitStatus = exitStatus;
	}

    /**
     * The status the application will exit.
     *
     * @return The status number.
     */
    public ExitStatus getExitStatus() {
        return exitStatus;
    }

}
