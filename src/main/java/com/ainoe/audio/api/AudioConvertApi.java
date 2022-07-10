package com.ainoe.audio.api;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.constvalue.ApiParamType;
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
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class AudioConvertApi extends RestfulBinaryStreamApiComponentBase {

    static Logger logger = LoggerFactory.getLogger(AudioConvertApi.class);

    static Map<String, Integer> bitRateMap = new HashMap<>();

    static {
        bitRateMap.put("128Kbs", 128000);
        bitRateMap.put("192Kbs", 192000);
        bitRateMap.put("320Kbs", 320000);
    }

    @Override
    public String getToken() {
        return "/audio/convert";
    }

    @Override
    public String getName() {
        return "音频格式转换";
    }

    @Input({
            @Param(name = "format", type = ApiParamType.ENUM, desc = "格式"),
            @Param(name = "bitRate", type = ApiParamType.ENUM, desc = "比特率")
    })
    @Description(desc = "音频格式转换")
    @Override
    public Object myDoService(JSONObject jsonObj, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(Config.AUDIO_HOME())) {
            throw new ConfigLostException("audio.home");
        }
        String format = jsonObj.getString("format");
        String bitRateStr = jsonObj.getString("bitRate");
        Integer bitRate = null;
        if (StringUtils.isBlank(format)) {
            format = AudioFormat.MP3.getValue();
        }
        AudioFormat audioFormat = AudioFormat.getAudioFormat(format);
        if (audioFormat == null) {
            audioFormat = AudioFormat.MP3;
        }
        if (AudioFormat.MP3.equals(audioFormat)) {
            if (StringUtils.isNotBlank(bitRateStr)) {
                bitRate = bitRateMap.get(bitRateStr);
            } else {
                bitRate = bitRateMap.get("320Kbs");
            }
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
            String outputFileName = filename.substring(0, filename.lastIndexOf(".") + 1) + audioFormat.getValue();
//            response.setHeader("Content-Disposition", " attachment; filename=\"" + outputFileName + "\"");
//            response.setContentType("application/octet-stream");
//            try (ServletOutputStream outputStream = response.getOutputStream()) {
//                convert(inputStream, outputStream, audioFormat);
//            } catch (Exception ex) {
//                logger.error(ex.getMessage(), ex);
//            }
            convert(inputStream, Config.AUDIO_HOME() + File.separator + outputFileName, audioFormat, bitRate);
        }

        return null;
    }

    public void convert(InputStream inputStream, String outputStream, AudioFormat format, Integer bitRate) throws Exception {
        Frame audioSamples;
        FFmpegFrameRecorder recorder = null;
        //抓取器
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
        try {
            // 开启抓取器
            grabber.start();
            recorder = new FFmpegFrameRecorder(outputStream, grabber.getAudioChannels());
            if (AudioFormat.MP3.equals(format)) {
                recorder.setAudioOption("crf", "0");
                recorder.setAudioBitrate(bitRate);
            }
            recorder.setAudioChannels(grabber.getAudioChannels());
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setFormat(format.getValue());
            recorder.setAudioMetadata(grabber.getAudioMetadata());
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

    public void convert(InputStream inputStream, OutputStream outputStream, AudioFormat format) throws Exception {
        Frame audioSamples;
        FFmpegFrameRecorder recorder = null;
        //抓取器
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
        try {
            // 开启抓取器
            grabber.start();
            recorder = new FFmpegFrameRecorder(outputStream, grabber.getAudioChannels());
            recorder.setAudioOption("crf", "0");
//            recorder.setAudioCodec(avcodec.AV_CODEC_ID_PCM_S16LE);
//            recorder.setAudioBitrate(320000);
            recorder.setAudioChannels(grabber.getAudioChannels());
            recorder.setSampleRate(44100);
            recorder.setAudioQuality(0);
            recorder.setFormat(format.getValue());
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
