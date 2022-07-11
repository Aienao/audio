package com.ainoe.audio.exception;


import com.ainoe.audio.exception.core.ApiRuntimeException;

public class AudioDownloadException extends ApiRuntimeException {

    private static final long serialVersionUID = -4517717905210993437L;

    public AudioDownloadException(String msg) {
        super("音频文件：" + msg + "下载失败");
    }

}
