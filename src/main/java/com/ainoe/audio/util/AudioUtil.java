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

public class AudioUtil {

    static Logger logger = LoggerFactory.getLogger(AudioUtil.class);

    public static Map<String, Integer> bitRateMap = new HashMap<>();

    static {
        bitRateMap.put("128Kbs", 128000);
        bitRateMap.put("192Kbs", 192000);
        bitRateMap.put("320Kbs", 320000);
    }

    public static void convert(InputStream inputStream, String outputPath, AudioFormat format, Integer bitRate) throws Exception {
        FFmpegFrameRecorder recorder = null;
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
        try {
            grabber.start();
            recorder = new FFmpegFrameRecorder(outputPath, grabber.getAudioChannels());
            recorder.setMetadata(grabber.getMetadata());
            if (AudioFormat.MP3.equals(format)) {
//                recorder.setAudioOption("crf", "0");
                recorder.setAudioBitrate(bitRate);
            }
            recorder.setAudioChannels(grabber.getAudioChannels());
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setFormat(format.getValue());
            recorder.start();
            Frame audioSamples;
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
