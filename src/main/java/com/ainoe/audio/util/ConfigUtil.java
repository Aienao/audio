package com.ainoe.audio.util;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.exception.AudioHomeNotFoundException;
import com.ainoe.audio.exception.ConfigLostException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigUtil {

    static Logger logger = LoggerFactory.getLogger(ConfigUtil.class);

    /**
     * 检查audio.home目录是否存在
     */
    public static void checkAudioHome() {
        if (StringUtils.isBlank(Config.AUDIO_HOME())) {
            throw new ConfigLostException("audio.home");
        }
        Path home = Paths.get(Config.AUDIO_HOME());
        if (!Files.exists(home) || !Files.isDirectory(home)) {
            throw new AudioHomeNotFoundException(Config.AUDIO_HOME());
        }
    }

    /**
     * 自动创建audio.home目录
     *
     * @param autoCreate 是否自动创建
     * @throws IOException
     */
    public static void createAudioHome(boolean autoCreate) throws IOException {
        if (StringUtils.isNotBlank(Config.AUDIO_HOME()) && autoCreate) {
            File home = new File(Config.AUDIO_HOME());
            if (!home.exists() || !home.isDirectory()) {
                Files.createDirectories(Paths.get(Config.AUDIO_HOME()));
                logger.info("auto create audio home: " + Config.AUDIO_HOME());
            }
        }
    }
}
