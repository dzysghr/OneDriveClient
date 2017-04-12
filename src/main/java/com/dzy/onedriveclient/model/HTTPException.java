package com.dzy.onedriveclient.model;


public class HTTPException extends Exception {

    public int code;

    public HTTPException(int code) {
        this.code = code;
    }

    public HTTPException(String message) {
        super(message);
    }
}
