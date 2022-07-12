package com.ainoe.audio.api;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.dto.AudioVo;
import com.ainoe.audio.restful.annotation.Description;
import com.ainoe.audio.restful.component.RestfulApiComponentBase;
import com.ainoe.audio.util.AudioUtil;
import com.ainoe.audio.util.ConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        ConfigUtil.checkAudioHome();
        Path path = Paths.get(Config.AUDIO_HOME());
        List<AudioVo> result = new ArrayList<>();
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (Files.isRegularFile(file)) {
                    FFmpegFrameGrabber grabber = null;
                    try (InputStream inputStream = Files.newInputStream(file);) {
                        grabber = new FFmpegFrameGrabber(inputStream);
                        grabber.start();
                        AVFormatContext formatContext = grabber.getFormatContext();
                        Map<String, String> metadata = grabber.getMetadata();
                        result.add(new AudioVo(file.toFile().getName()
                                , grabber.getFormat()
                                , grabber.getSampleRate()
                                , formatContext.bit_rate()
                                , grabber.getAudioChannels()
                                , AudioUtil.getDuration(formatContext.duration())
                                , metadata.get("date")
                                , metadata.get("artist")
                                , metadata.get("album")
                                , metadata.get("track")
                                , metadata.get("title")
                                , Files.size(file)));
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    } finally {
                        if (grabber != null) {
                            grabber.stop();
                        }
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return result;
    }

}
