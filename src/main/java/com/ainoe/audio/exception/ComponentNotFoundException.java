package com.ainoe.audio.exception;


import com.ainoe.audio.exception.core.ApiRuntimeException;

public class ComponentNotFoundException extends ApiRuntimeException {
    private static final long serialVersionUID = -6165807991291970685L;

    public ComponentNotFoundException(String msg) {
        super(msg);
    }

}
