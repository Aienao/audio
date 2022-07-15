package com.ainoe.audio.schedule;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.util.AudioUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;

import java.io.IOException;

@Configuration
@EnableScheduling
public class AutoCleanAudioJob implements SchedulingConfigurer {

    static Logger logger = LoggerFactory.getLogger(AutoCleanAudioJob.class);

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if (Config.AUDIO_AUDIO_CLEAN() && Config.AUDIO_RETAIN_DAYS() > 0) {
            String cron = Config.AUDIO_AUDIO_CLEAN_CRON();
            if (!CronExpression.isValidExpression(cron)) {
                cron = "0 0 0 * * ?";
            }
            String finalCron = cron;
            taskRegistrar.addTriggerTask(() -> {
                try {
                    AudioUtil.cleanAudioHome();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }, triggerContext -> new CronTrigger(finalCron).nextExecutionTime(triggerContext));
        }
    }
}
