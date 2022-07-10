package com.ainoe.audio.service;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.exception.AudioHomeNotFoundException;
import com.ainoe.audio.exception.ConfigLostException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ConfigServiceImpl implements ConfigService {

    @Override
    public void checkAudioHome() {
        if (StringUtils.isBlank(Config.AUDIO_HOME())) {
            throw new ConfigLostException("audio.home");
        }
        Path home = Paths.get(Config.AUDIO_HOME());
        if (!Files.exists(home) || !Files.isDirectory(home)) {
            throw new AudioHomeNotFoundException(Config.AUDIO_HOME());
        }
    }
}
