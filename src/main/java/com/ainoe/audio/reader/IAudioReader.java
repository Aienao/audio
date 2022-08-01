package com.ainoe.audio.reader;

import com.ainoe.audio.dto.AudioVo;

import java.io.File;

public interface IAudioReader {

    String getName();

    AudioVo getAudioMetadata(File file) throws Exception;
}
