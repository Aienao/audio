package com.ainoe.audio.constvalue;

public enum AudioFormat {
    FLAC("flac"),
    MP3("mp3"),
    WAV("wav"),
    APE("ape"),
    ;
    private final String value;

    AudioFormat(String _name) {
        this.value = _name;
    }

    public String getValue() {
        return value;
    }

    public static AudioFormat getAudioFormat(String name) {
        for (AudioFormat format : values()) {
            if (format.value.equals(name)) {
                return format;
            }
        }
        return null;
    }
}
