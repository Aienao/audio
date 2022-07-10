package com.ainoe.audio.util;

import com.ainoe.audio.constvalue.AudioFormat;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 音频工具类
 *
 * @author Aienao
 */
public class AudioUtil {

    static Logger logger = LoggerFactory.getLogger(AudioUtil.class);

    public static Map<String, Integer> bitRateMap = new HashMap<>(); // 音频比特率

    static {
        bitRateMap.put("128Kbs", 128000);
        bitRateMap.put("192Kbs", 192000);
        bitRateMap.put("320Kbs", 320000);
    }

    /**
     * 音频格式转换
     *
     * @param inputStream 待转换的音频流
     * @param outputPath  输出音频文件路径
     * @param format      音频格式
     * @param bitRate     比特率
     * @throws Exception
     */
    public static void convert(InputStream inputStream, String outputPath, AudioFormat format, Integer bitRate) throws Exception {
        FFmpegFrameRecorder recorder = null;
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
        try {
            // 开始抓取音频
            grabber.start();
            recorder = new FFmpegFrameRecorder(outputPath, grabber.getAudioChannels());
            // 还原音频元数据
            recorder.setMetadata(grabber.getMetadata());
            if (AudioFormat.MP3.equals(format)) {
                // 设置固定比特率
//                recorder.setAudioOption("crf", "0");
                // 设置比特率（setAudioBitrate与setAudioQuality不可混用，否则设定的比特率不生效）
                recorder.setAudioBitrate(bitRate);
            }
            // 设置音频声道数
            recorder.setAudioChannels(grabber.getAudioChannels());
            // 设置音频采样率
            recorder.setSampleRate(grabber.getSampleRate());
            // 设置音频格式（javacv将根据格式自动选择编码器）
            recorder.setFormat(format.getValue());
            recorder.start();
            Frame audioSamples;
            // 读取音频
            while ((audioSamples = grabber.grabSamples()) != null) {
                recorder.setTimestamp(grabber.getTimestamp());
                recorder.record(audioSamples);
            }
            // 关闭资源（必须）
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
