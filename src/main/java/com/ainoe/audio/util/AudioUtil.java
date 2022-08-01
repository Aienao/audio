package com.ainoe.audio.util;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.constvalue.AudioFormat;
import com.ainoe.audio.dto.AudioVo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
            // 构造记录器并设置声道数为原始值
            recorder = new FFmpegFrameRecorder(outputPath, grabber.getAudioChannels());
            // 还原音频元数据
            recorder.setMetadata(grabber.getMetadata());
            if (AudioFormat.MP3.equals(format)) {
                // 设置固定比特率
//                recorder.setAudioOption("crf", "0");
                // 设置比特率（setAudioBitrate与setAudioQuality不可混用，否则设定的比特率不生效）
                recorder.setAudioBitrate(bitRate);
            }
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
        } catch (Exception ex) {
            logger.error("audio convert failed.\n{}", ExceptionUtils.getStackTrace(ex));
            throw ex;
        } finally {
            if (recorder != null) {
                recorder.stop();
            }
            grabber.stop();
        }
    }

    /**
     * 格式化时长为{分:秒}字符串，精确到秒，格式为00:00
     *
     * @param duration 时长
     * @return
     */
    public static String getDuration(long duration) {
        long min = duration / (1000L * 1000 * 60);
        long sec = duration % (1000L * 1000 * 60) / 1000000;
        return (min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec);
    }

    /**
     * Firefox浏览器userAgent：Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0
     * Chrome浏览器userAgent：Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.85 Safari/537.36
     * Edg浏览器userAgent：Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.85 Safari/537.36 Edg/90.0.818.46
     *
     * @param userAgent
     * @param fileName
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getEncodedFileName(String userAgent, String fileName) throws UnsupportedEncodingException {
        if (userAgent.indexOf("Gecko") > 0) {
            //chrome、firefox、edge浏览器下载文件
            fileName = URLEncoder.encode(fileName, "UTF-8");
            fileName = fileNameSpecialCharacterHandling(fileName);
        } else {
            fileName = new String(fileName.replace(" ", "").getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        }
        return fileName;
    }

    /**
     * 清理audio.home
     *
     * @throws IOException
     */
    public static void cleanAudioHome() throws IOException {
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
    }

    /**
     * 使用FFmpeg获取音频元数据
     * 此方式无法获取专辑封面，性能也较差，优点在于所有格式的音频都可用此方法读取元数据
     *
     * @param grabber FFmpegFrameGrabber对象，从方法外构建并传入，释放也需要在方法外
     * @param file    目标音频文件
     * @return
     * @throws IOException
     */
    public static AudioVo getAudioMetaDataByFFmpeg(FFmpegFrameGrabber grabber, Path file) throws IOException {
        AVFormatContext formatContext = grabber.getFormatContext();
        Map<String, String> metadata = grabber.getMetadata();
        return new AudioVo(file.toFile().getName()
                , grabber.getFormat()
                , grabber.getSampleRate()
                , formatContext.bit_rate()
                , grabber.getAudioChannels()
                , getDuration(formatContext.duration())
                , metadata.get("date")
                , metadata.get("artist")
                , metadata.get("album")
                , metadata.get("track")
                , metadata.get("title")
                , Files.size(file));
    }

    /**
     * chrome、firefox、edge浏览器下载文件时，文件名包含~@#$&+=;这八个英文字符时会变成乱码_%40%23%24%26%2B%3D%3B，
     * 下面是对@#$&+=;这七个字符做特殊处理，
     * 对于~这个字符还是会变成下划线_，暂无法处理
     *
     * @param fileName 文件名
     * @return 返回处理后的文件名
     */
    private static String fileNameSpecialCharacterHandling(String fileName) {
        if (fileName.contains("%40")) {
            fileName = fileName.replace("%40", "@");
        }
        if (fileName.contains("%23")) {
            fileName = fileName.replace("%23", "#");
        }
        if (fileName.contains("%24")) {
            fileName = fileName.replace("%24", "$");
        }
        if (fileName.contains("%26")) {
            fileName = fileName.replace("%26", "&");
        }
        if (fileName.contains("%2B")) {
            fileName = fileName.replace("%2B", "+");
        }
        if (fileName.contains("%3D")) {
            fileName = fileName.replace("%3D", "=");
        }
        if (fileName.contains("%3B")) {
            fileName = fileName.replace("%3B", ";");
        }
        return fileName;
    }
}
