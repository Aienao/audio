package com.ainoe.audio.exception;


import com.ainoe.audio.exception.core.ApiRuntimeException;

public class ConfigLostException extends ApiRuntimeException {

    private static final long serialVersionUID = 5741189388758764966L;

    public ConfigLostException(String msg) {
        super("请先配置：" + msg);
    }

}
