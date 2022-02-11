package com.silibrina.tecnova.commons.messenger;

public enum MessageStatus {
    UNDEFINED_STATUS(-1),
    ENTRY_NOT_FOUND(-2),
    IO_ERROR(-3),
    UNKNOWN_ERROR(-4);

    public int status;

    MessageStatus(int status) {
        this.status = status;
    }

}
