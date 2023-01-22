package com.google.ar.core.codelabs.arlocalizer.netservice.Exception;

public class ServerException extends RuntimeException {
    private int code;
    private String msg;

    public ServerException(int code,String msg){
        this.code=code;
        this.msg=msg;
    }
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return msg;
    }

}
