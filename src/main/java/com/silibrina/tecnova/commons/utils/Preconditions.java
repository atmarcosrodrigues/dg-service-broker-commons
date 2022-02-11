package com.silibrina.tecnova.commons.utils;

import com.silibrina.tecnova.commons.exceptions.ExitStatus;
import com.silibrina.tecnova.commons.exceptions.InvalidConditionException;
import com.silibrina.tecnova.commons.exceptions.UnrecoverableErrorException;
import com.silibrina.tecnova.commons.messenger.MessageStatus;

import java.io.IOException;

/**
 * General class to perform test over condition. The common behavior here is to
 * throw an exception if the condition is not satisfied.
 */
public final class Preconditions {

    /**
     * Checks if a condition is true. If not, it throws an exception with the
     * given message.
     *
     * @param message
     *            The message of the exception in case the condition is false.
     * @param condition
     *            The condition to be tested.
     * @throws InvalidConditionException
     *             If the condition if is not satisfied (false).
     */
    public static void checkCondition(String message, boolean condition)
            throws InvalidConditionException {
        if (!condition) {
            throw new InvalidConditionException(message);
        }
    }

    /**
     * Checks if a condition is true. If not, it throws an exception with the
     * given message. This condition must be related with a file or stream error.
     *
     * @param message
     *            The message of the exception in case the condition is false.
     * @param condition
     *            The condition to be tested.
     * @throws IOException
     *             If the condition if is not satisfied (false).
     */
    public static void checkIOCondition(String message, boolean condition) throws IOException {
        if (!condition) {
            throw new IOException(message);
        }
    }

    /**
     * Checks if an object is not null. If it is, it throws an exception with
     * the given message.
     *
     * @param message
     *            The message of the exception in case the object is null.
     * @param obj
     *            The object to be tested.
     * @throws InvalidConditionException
     *             If the condition if is not satisfied (false).
     */
    public static void checkNotNullCondition(String message, Object obj)
            throws InvalidConditionException {
        if (obj == null) {
            throw new InvalidConditionException(message);
        }
    }

    /**
     * Checks if an object is not null. If it is, it throws an exception with
     * the given message.
     *
     * @param message
     *            The message of the exception in case the object is null.
     * @param status a representation of the status that the exception must carry.
     * @param obj
     *            The object to be tested.
     * @throws InvalidConditionException
     *             If the condition if is not satisfied (false).
     */
    public static void checkNotNullCondition(String message, MessageStatus status, Object obj)
            throws InvalidConditionException {
        if (obj == null) {
            throw new InvalidConditionException(message, status);
        }
    }

    /**
     * Checks if an object is not null. If it is, it throws an exception with
     * the given message. This is an exception for unrecoverable error.
     *
     * @param message
     *            The message of the exception in case the object is null.
     * @param exitStatus
     *            A status to be passed to the OS as exit status
     * @param obj
     *            The object to be tested.
     * @throws InvalidConditionException
     *             If the condition if is not satisfied (false).
     */
    public static void checkNotNullCondition(String message, ExitStatus exitStatus, Object obj)
            throws UnrecoverableErrorException {
        if (obj == null) {
            throw new UnrecoverableErrorException(message, exitStatus);
        }
    }

    /**
     * Checks if a String is null, empty or only white space. If it is, it
     * throws an exception with the given message.
     *
     * @param message The message of the exception in case the String is invalid.
     * @param toCheck The String to be tested.
     *
     * @throws InvalidConditionException If the condition if is not satisfied (null or empty String).
     */
    public static void checkValidString(String message, String toCheck)
            throws InvalidConditionException {
        if (!checkValidString(toCheck)) {
            throw new InvalidConditionException(message);
        }
    }

    /**
     * Checks if a {@link String} exceeds a maximum length, if that is the case, it
     * throws an exception with the given message.
     *
     * @param message The message of the exception in case the String is invalid.
     * @param toCheck The String to be tested.
     * @param maxSize maximum size for the given string.
     *
     * @throws InvalidConditionException If the condition if is not satisfied (null or empty String).
     */
    public static void checkOptionalValidString(String message, String toCheck, int maxSize) {
        if (toCheck != null && toCheck.length() > maxSize) {
            throw new InvalidConditionException(message);
        }
    }

    /**
     * Checks if a String is null, empty or only white space. If it is, it
     * throws an exception with the given message.
     *
     * @param message The message of the exception in case the String is invalid.
     * @param exitStatus
     *            A status to be passed to the OS as exit status
     * @param toCheck The String to be tested.
     *
     * @throws UnrecoverableErrorException If the condition if is not satisfied (null or empty String).
     */
    public static void checkValidString(String message, ExitStatus exitStatus, String toCheck)
            throws UnrecoverableErrorException {
        if (!checkValidString(toCheck)) {
            throw new UnrecoverableErrorException(message, exitStatus);
        }
    }

    private static boolean checkValidString(String toCheck) {
        return !(toCheck == null || toCheck.trim().length() == 0);
    }
}
