package com.ainoe.audio.config;

import com.ainoe.audio.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

@Configuration
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static final String CONFIG_FILE = "application.properties";
    private static String AUDIO_HOME; // 音频文件根目录
    private static Boolean AUDIO_AUDIO_CLEAN; // 是否自动清理audio.home下的audio
    private static String AUDIO_AUDIO_CLEAN_CRON; // 自动清理audio.home作业cron
    private static Integer AUDIO_RETAIN_DAYS; // audio保留天数

    public static String AUDIO_HOME() {
        return AUDIO_HOME;
    }

    public static Boolean AUDIO_AUDIO_CLEAN() {
        return AUDIO_AUDIO_CLEAN;
    }

    public static String AUDIO_AUDIO_CLEAN_CRON() {
        return AUDIO_AUDIO_CLEAN_CRON;
    }

    public static Integer AUDIO_RETAIN_DAYS() {
        return AUDIO_RETAIN_DAYS;
    }

    @PostConstruct
    public void init() {
        try {
            Properties prop = new Properties();
            prop.load(new InputStreamReader(Objects.requireNonNull(Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)), StandardCharsets.UTF_8));
            AUDIO_HOME = prop.getProperty("audio.home");
            boolean audioHomeAutoCreate = Boolean.parseBoolean(prop.getProperty("audio.home.auto.create", "true"));
            AUDIO_AUDIO_CLEAN = Boolean.parseBoolean(prop.getProperty("audio.auto.clean", "false"));
            AUDIO_AUDIO_CLEAN_CRON = prop.getProperty("audio.auto.clean.cron", "0 0 0 * * ?");
            AUDIO_RETAIN_DAYS = Integer.parseInt(prop.getProperty("audio.retain.days"));
            ConfigUtil.createAudioHome(audioHomeAutoCreate);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
