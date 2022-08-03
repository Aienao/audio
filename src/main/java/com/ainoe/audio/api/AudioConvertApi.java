package com.ainoe.audio.api;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.constant.ApiParamType;
import com.ainoe.audio.constant.AudioFormat;
import com.ainoe.audio.exception.FileSuffixLostException;
import com.ainoe.audio.multithread.batch.BatchRunner;
import com.ainoe.audio.restful.annotation.Description;
import com.ainoe.audio.restful.annotation.Input;
import com.ainoe.audio.restful.annotation.Param;
import com.ainoe.audio.restful.component.RestfulBinaryStreamApiComponentBase;
import com.ainoe.audio.util.AudioUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.List;

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

    @Input({
            @Param(name = "format", type = ApiParamType.ENUM, desc = "格式"),
            @Param(name = "bitRate", type = ApiParamType.ENUM, desc = "比特率")
    })
    @Description(desc = "音频格式转换")
    @Override
    public Object myDoService(JSONObject jsonObj, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uuid = jsonObj.getString("uuid");
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
                bitRate = AudioUtil.bitRateMap.get(bitRateStr);
            } else {
                bitRate = AudioUtil.bitRateMap.get("320Kbs");
            }
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> fileList = multipartRequest.getFiles("fileList");
        if (CollectionUtils.isNotEmpty(fileList)) {
            BatchRunner<MultipartFile> runner = new BatchRunner<>();
            AudioFormat finalAudioFormat = audioFormat;
            Integer finalBitRate = bitRate;
            runner.execute(fileList, 5, item -> {
                String filename = item.getOriginalFilename();
                if (!filename.contains(".")) {
                    throw new FileSuffixLostException(filename);
                }
                try (InputStream inputStream = item.getInputStream()) {
                    String outputFileName = filename.substring(0, filename.lastIndexOf(".") + 1) + finalAudioFormat.getValue();
                    AudioUtil.convert(inputStream, Config.AUDIO_HOME() + File.separator + uuid + File.separator + outputFileName, finalAudioFormat, finalBitRate);
                } catch (Exception ex) {
                    logger.error("{} convert failed.", filename);
                    logger.error(ex.getMessage(), ex);
                }
            });
        }
        return null;
    }

}
