package com.ainoe.audio.exception;


import com.ainoe.audio.exception.core.ApiRuntimeException;

public class ApiNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -8529977350164125804L;

    public ApiNotFoundException(String msg) {
        super(msg);
    }

}
