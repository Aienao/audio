package com.ainoe.audio.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

@Configuration
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static final String CONFIG_FILE = "application.properties";
    private static String AUDIO_HOME; // 音频文件根目录

    public static String AUDIO_HOME() {
        return AUDIO_HOME;
    }

    @PostConstruct
    public void init() {
        try {
            Properties prop = new Properties();
            prop.load(new InputStreamReader(Objects.requireNonNull(Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)), StandardCharsets.UTF_8));
            AUDIO_HOME = prop.getProperty("audio.home");
            boolean audioHomeAutoCreate = Boolean.parseBoolean(prop.getProperty("audio.home.auto.create", "true"));
            if (StringUtils.isNotBlank(AUDIO_HOME) && audioHomeAutoCreate) {
                File home = new File(AUDIO_HOME);
                if (!home.exists() || !home.isDirectory()) {
                    Files.createDirectory(Paths.get(AUDIO_HOME));
                    logger.info("auto create audio home: " + AUDIO_HOME);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
