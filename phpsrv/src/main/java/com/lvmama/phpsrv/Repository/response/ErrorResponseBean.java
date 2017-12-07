package com.lvmama.phpsrv.Repository.response;

/**
 * Created by libiying on 2017/1/3.
 */
public class ErrorResponseBean {

    private int error;

    private String message;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
