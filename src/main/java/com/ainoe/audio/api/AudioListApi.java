package com.ainoe.audio.api;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.dto.AudioVo;
import com.ainoe.audio.exception.AudioReadNotFoundException;
import com.ainoe.audio.exception.FileSuffixLostException;
import com.ainoe.audio.reader.AudioReaderFactory;
import com.ainoe.audio.reader.IAudioReader;
import com.ainoe.audio.restful.annotation.Description;
import com.ainoe.audio.restful.component.RestfulApiComponentBase;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

@Component
public class AudioListApi extends RestfulApiComponentBase {

    static Logger logger = LoggerFactory.getLogger(AudioListApi.class);

    @Override
    public String getToken() {
        return "/audio/list";
    }

    @Override
    public String getName() {
        return "音频列表";
    }

    @Description(desc = "音频列表")
    @Override
    public Object myDoService(JSONObject jsonObj) throws IOException {
        String uuid = jsonObj.getString("uuid");
        Path path = Paths.get(Config.AUDIO_HOME() + File.separator + uuid);
        List<AudioVo> result = new ArrayList<>();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path subPath, BasicFileAttributes attrs) throws IOException {
                if (Files.isRegularFile(subPath)) {
                    try {
                        File file = subPath.toFile();
                        if (!file.getName().contains(".")) {
                            throw new FileSuffixLostException(file.getName());
                        }
                        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                        IAudioReader handler = AudioReaderFactory.getHandler(suffix);
                        if (handler == null) {
                            throw new AudioReadNotFoundException(suffix);
                        }
                        result.add(handler.getAudioMetadata(file));
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return result;
    }

}
