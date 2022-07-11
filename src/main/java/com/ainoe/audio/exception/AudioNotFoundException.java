package com.ainoe.audio.exception;


import com.ainoe.audio.exception.core.ApiRuntimeException;

public class AudioNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = -6859067606780940559L;

    public AudioNotFoundException(String msg) {
        super("音频文件：" + msg + "不存在");
    }

}
