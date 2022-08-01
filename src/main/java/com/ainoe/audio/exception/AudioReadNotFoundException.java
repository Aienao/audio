package com.ainoe.audio.exception;


import com.ainoe.audio.exception.core.ApiRuntimeException;

public class AudioReadNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -8850006127770565843L;

    public AudioReadNotFoundException(String msg) {
        super("audio reader : " + msg + "不存在");
    }

}
