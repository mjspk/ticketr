package com.ensf614.ticketr.model;

public class Response<T> {
    private T data;
    private String message;
    private boolean success;


    public Response(T data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    public Response() {
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    
}
