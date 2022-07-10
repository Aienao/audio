package com.ainoe.audio.exception;


import com.ainoe.audio.exception.core.ApiRuntimeException;

public class AudioHomeNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 5741189388758764966L;

    public AudioHomeNotFoundException(String msg) {
        super("audio.home：" + msg + "不存在");
    }

}
