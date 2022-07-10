package com.ainoe.audio.exception;


import com.ainoe.audio.exception.core.ApiRuntimeException;

public class UnknownAudioFormatException extends ApiRuntimeException {

    private static final long serialVersionUID = 9141689012029954982L;

    public UnknownAudioFormatException(String msg) {
        super("未知的音频格式：" + msg);
    }

}
