package com.ainoe.audio.api;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.constvalue.ApiParamType;
import com.ainoe.audio.constvalue.AudioFormat;
import com.ainoe.audio.exception.FileNameSuffixLostException;
import com.ainoe.audio.exception.UnknownAudioFormatException;
import com.ainoe.audio.restful.annotation.Description;
import com.ainoe.audio.restful.annotation.Input;
import com.ainoe.audio.restful.annotation.Param;
import com.ainoe.audio.restful.component.RestfulBinaryStreamApiComponentBase;
import com.ainoe.audio.service.ConfigService;
import com.ainoe.audio.util.AudioUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

@Component
public class AudioConvertApi extends RestfulBinaryStreamApiComponentBase {

    static Logger logger = LoggerFactory.getLogger(AudioConvertApi.class);

    @Resource
    ConfigService configService;

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
        configService.checkAudioHome();
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
            logger.debug("converting {}.", filename);
            String outputFileName = filename.substring(0, filename.lastIndexOf(".") + 1) + audioFormat.getValue();
            AudioUtil.convert(inputStream, Config.AUDIO_HOME() + File.separator + outputFileName, audioFormat, bitRate);
        }
        return null;
    }

}
