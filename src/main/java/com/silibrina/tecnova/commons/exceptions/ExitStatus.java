package com.silibrina.tecnova.commons.exceptions;

/**
 * Exit status used when exiting this process.
 * This exit (kill) can happen for an internal error (exitStatus greater than 0) or
 * a simple process stop (existStatus = 0)
 */
public enum ExitStatus {
    SUCCESS_STATUS(0),
    DEFAULT_ERROR_STATUS(1),
    CONFIGURATION_ERROR_STATUS(2),
    DB_ERROR_STATUS(3),
    ROUTE_ERROR_STATUS(4),
    INDEXER_ERROR_STATUS(5),
    DEVELOPER_ERROR_STATUS(6),
    IO_ERROR_STATUS(7),
    NOT_IMPLEMENTED_ERROR_STATUS(8);

    public final int exitStatus;

    ExitStatus(int exitStatus) {
        this.exitStatus = exitStatus;
    }
}
