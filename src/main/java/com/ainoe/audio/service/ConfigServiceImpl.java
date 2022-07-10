package com.ainoe.audio.service;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.exception.AudioHomeNotFoundException;
import com.ainoe.audio.exception.ConfigLostException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ConfigServiceImpl implements ConfigService {

    @Override
    public void checkAudioHome() {
        if (StringUtils.isBlank(Config.AUDIO_HOME())) {
            throw new ConfigLostException("audio.home");
        }
        File home = new File(Config.AUDIO_HOME());
        if (!home.exists() || !home.isDirectory()) {
            throw new AudioHomeNotFoundException(Config.AUDIO_HOME());
        }
    }
}
