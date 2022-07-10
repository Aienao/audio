package com.ainoe.audio.exception;


import com.ainoe.audio.exception.core.ApiRuntimeException;

public class FileNameSuffixLostException extends ApiRuntimeException {

    private static final long serialVersionUID = 3085497166854068545L;

    public FileNameSuffixLostException(String msg) {
        super("文件：" + msg + "缺少后缀名");
    }

}
