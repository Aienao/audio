package com.ainoe.audio.api;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.constvalue.AudioFormat;
import com.ainoe.audio.exception.core.ApiRuntimeException;
import com.ainoe.audio.restful.annotation.Description;
import com.ainoe.audio.restful.annotation.Input;
import com.ainoe.audio.restful.annotation.Param;
import com.ainoe.audio.restful.component.RestfulBinaryStreamApiComponentBase;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.bytedeco.javacv.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
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
    public Object myDoService(JSONObject jsonObj, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (StringUtils.isBlank(Config.AUDIO_HOME())) {
            throw new ApiRuntimeException("请先配置audio.home");
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
            convert(inputStream, Config.AUDIO_HOME() + File.separator + filename.substring(0, filename.lastIndexOf(".") + 1) + audioFormat.getName(), audioFormat);
        }

        return null;
    }

    public void convert(InputStream inputStream, String outputStream, AudioFormat format) throws IOException {
        Frame audioSamples = null;
        // 音频录制（输出地址，音频通道）
        FFmpegFrameRecorder recorder = null;
        //抓取器
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);

        // 开启抓取器
        if (start(grabber)) {
            recorder = new FFmpegFrameRecorder(outputStream, grabber.getAudioChannels());
            recorder.setAudioOption("crf", "0");
            recorder.setAudioCodec(format.getAudioCodec());
            recorder.setAudioBitrate(grabber.getAudioBitrate());
            recorder.setAudioChannels(grabber.getAudioChannels());
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setAudioQuality(0);
            recorder.setFormat(format.getName());
            // 开启录制器
            if (start(recorder)) {
                try {
                    // 抓取音频
                    while ((audioSamples = grabber.grabSamples()) != null) {
                        recorder.setTimestamp(grabber.getTimestamp());
                        recorder.record(audioSamples);
                    }
                } catch (org.bytedeco.javacv.FrameGrabber.Exception e1) {
                    System.err.println("抓取失败");
                } catch (Exception e) {
                    System.err.println("录制失败");
                }
                stop(recorder);
                stop(grabber);
            }
        }

    }

    public boolean start(FrameGrabber grabber) {
        try {
            grabber.start();
            return true;
        } catch (org.bytedeco.javacv.FrameGrabber.Exception e2) {
            try {
                System.err.println("首次打开抓取器失败，准备重启抓取器...");
                grabber.restart();
                return true;
            } catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
                try {
                    System.err.println("重启抓取器失败，正在关闭抓取器...");
                    grabber.stop();
                } catch (org.bytedeco.javacv.FrameGrabber.Exception e1) {
                    System.err.println("停止抓取器失败！");
                }
            }

        }
        return false;
    }

    public boolean start(FrameRecorder recorder) {
        try {
            recorder.start();
            return true;
        } catch (Exception e2) {
            try {
                System.err.println("首次打开录制器失败！准备重启录制器...");
                recorder.stop();
                recorder.start();
                return true;
            } catch (Exception e) {
                try {
                    System.err.println("重启录制器失败！正在停止录制器...");
                    recorder.stop();
                } catch (Exception e1) {
                    System.err.println("关闭录制器失败！");
                }
            }
        }
        return false;
    }

    public boolean stop(FrameGrabber grabber) {
        try {
            grabber.flush();
            grabber.stop();
            return true;
        } catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
            return false;
        } finally {
            try {
                grabber.stop();
            } catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
                System.err.println("关闭抓取器失败");
            }
        }
    }

    public boolean stop(FrameRecorder recorder) {
        try {
            recorder.stop();
//            recorder.release();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                recorder.stop();
            } catch (Exception e) {

            }
        }
    }

}
