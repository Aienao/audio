package com.ainoe.audio.api;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.constvalue.AudioFormat;
import com.ainoe.audio.exception.ConfigLostException;
import com.ainoe.audio.exception.FileNameSuffixLostException;
import com.ainoe.audio.exception.UnknownAudioFormatException;
import com.ainoe.audio.restful.annotation.Description;
import com.ainoe.audio.restful.annotation.Input;
import com.ainoe.audio.restful.annotation.Param;
import com.ainoe.audio.restful.component.RestfulBinaryStreamApiComponentBase;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bytedeco.javacv.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

@Component
public class AudioConvertApi extends RestfulBinaryStreamApiComponentBase {

    static Logger logger = LoggerFactory.getLogger(AudioConvertApi.class);

    @Override
    public String getToken() {
        return "/audio/convert";
    }

    @Override
    public String getName() {
        return "音频格式转换";
    }

    @Input({@Param(name = "format", desc = "格式")})
    @Description(desc = "音频格式转换")
    @Override
    public Object myDoService(JSONObject jsonObj, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(Config.AUDIO_HOME())) {
            throw new ConfigLostException("audio.home");
        }
        String format = jsonObj.getString("format");
        if (StringUtils.isBlank(format)) {
            format = AudioFormat.FLAC.getName();
        }
        AudioFormat audioFormat = AudioFormat.getAudioFormat(format);
        if (audioFormat == null) {
            audioFormat = AudioFormat.FLAC;
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (MapUtils.isNotEmpty(fileMap)) {
            // todo 支持多个文件
            MultipartFile multipartFile = fileMap.entrySet().stream().findFirst().get().getValue();
            InputStream inputStream = multipartFile.getInputStream();
            String filename = multipartFile.getOriginalFilename();
            if (!filename.contains(".")) {
                throw new FileNameSuffixLostException(filename);
            }
            String suffix = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            AudioFormat sourceFormat = AudioFormat.getAudioFormat(suffix);
            if (sourceFormat == null) {
                throw new UnknownAudioFormatException(suffix);
            }
            convert(inputStream, Config.AUDIO_HOME() + File.separator + filename.substring(0, filename.lastIndexOf(".") + 1) + audioFormat.getName(), audioFormat);
        }

        return null;
    }

    public void convert(InputStream inputStream, String outputStream, AudioFormat format) throws Exception {
        Frame audioSamples;
        FFmpegFrameRecorder recorder = null;
        //抓取器
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
        try {
            // 开启抓取器
            grabber.start();
            recorder = new FFmpegFrameRecorder(outputStream, grabber.getAudioChannels());
//            recorder.setAudioOption("crf", "0");
//            recorder.setAudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE);
//            recorder.setAudioBitrate(grabber.getAudioBitrate());
//            recorder.setAudioChannels(grabber.getAudioChannels());
//            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setAudioQuality(0);
            recorder.setFormat(format.getName());
            // 开启录制器
            recorder.start();
            // 抓取音频
            while ((audioSamples = grabber.grabSamples()) != null) {
                recorder.setTimestamp(grabber.getTimestamp());
                recorder.record(audioSamples);
            }
            recorder.stop();
            grabber.flush();
            grabber.stop();
        } catch (Exception e) {
            logger.error("audio convert failed.{}", ExceptionUtils.getStackTrace(e));
            throw e;
        } finally {
            if (recorder != null) {
                recorder.stop();
            }
            grabber.stop();
        }

    }

}
