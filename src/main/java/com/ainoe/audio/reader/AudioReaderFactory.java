package com.ainoe.audio.reader;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 音频元数据读取类工厂
 */
@Component
public class AudioReaderFactory implements ApplicationContextAware {

    private static final Map<String, IAudioReader> componentMap = new HashMap<>();

    public static IAudioReader getHandler(String audioFormat) {
        return componentMap.get(audioFormat);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, IAudioReader> map = applicationContext.getBeansOfType(IAudioReader.class);
        for (Map.Entry<String, IAudioReader> entry : map.entrySet()) {
            IAudioReader reader = entry.getValue();
            componentMap.put(reader.getName(), entry.getValue());
        }
    }
}
