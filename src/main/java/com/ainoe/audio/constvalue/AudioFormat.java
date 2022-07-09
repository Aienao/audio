package com.ainoe.audio.constvalue;

import org.bytedeco.ffmpeg.global.avcodec;

public enum AudioFormat {
    FLAC("flac", avcodec.AV_CODEC_ID_FLAC),
    MP3("mp3", avcodec.AV_CODEC_ID_MP3),
    ;
    private final String name;
    private final Integer audioCodec;

    AudioFormat(String _name, Integer _audioCodec) {
        this.name = _name;
        this.audioCodec = _audioCodec;
    }

    public String getName() {
        return name;
    }

    public Integer getAudioCodec() {
        return audioCodec;
    }

    public static AudioFormat getAudioFormat(String name) {
        for (AudioFormat format : values()) {
            if (format.name.equals(name)) {
                return format;
            }
        }
        return null;
    }
}
