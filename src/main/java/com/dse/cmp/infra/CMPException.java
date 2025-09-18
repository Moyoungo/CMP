package com.dse.cmp.infra;

/** Runtime exception with error code. */
public final class CMPException extends RuntimeException {
    private final ErrorCodes code;
    public CMPException(ErrorCodes code, String message) { super(message); this.code = code; }
    public CMPException(ErrorCodes code, String message, Throwable cause) { super(message, cause); this.code = code; }
    public ErrorCodes code(){ return code; }
}
