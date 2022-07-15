package com.ainoe.audio.schedule;

import com.ainoe.audio.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Configuration
@EnableScheduling
public class AutoCleanAudioJob {

    static Logger logger = LoggerFactory.getLogger(AutoCleanAudioJob.class);

    // 每天0时0分0秒执行作业
    @Scheduled(cron = "0 0 0 * * ?")
    public void autoClean() {
        if (Config.AUDIO_AUDIO_CLEAN() && Config.AUDIO_RETAIN_DAYS() > 0) {
            try {
                LocalDateTime expiredTime = LocalDateTime.now().minusDays(Config.AUDIO_RETAIN_DAYS());
                Path path = Paths.get(Config.AUDIO_HOME());
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (Files.isRegularFile(file)) {
                            LocalDateTime lastModifiedTime = Files.getLastModifiedTime(file).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                            if (lastModifiedTime.isBefore(expiredTime)) {
                                Files.delete(file);
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
