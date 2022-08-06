package com.ainoe.audio.api;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.constant.ApiParamType;
import com.ainoe.audio.exception.AudioDownloadException;
import com.ainoe.audio.exception.AudioExpiredException;
import com.ainoe.audio.exception.core.ApiRuntimeException;
import com.ainoe.audio.restful.annotation.Description;
import com.ainoe.audio.restful.annotation.Input;
import com.ainoe.audio.restful.annotation.Param;
import com.ainoe.audio.restful.component.RestfulBinaryStreamApiComponentBase;
import com.ainoe.audio.util.AudioUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class AudioDownloadApi extends RestfulBinaryStreamApiComponentBase {

    static Logger logger = LoggerFactory.getLogger(AudioDownloadApi.class);

    @Override
    public String getToken() {
        return "/audio/download/{name}";
    }

    @Override
    public String getName() {
        return "音频下载";
    }

    @Input({
            @Param(name = "name", type = ApiParamType.STRING, isRequired = true, desc = "文件名称"),
    })
    @Description(desc = "音频下载")
    @Override
    public Object myDoService(JSONObject jsonObj, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uuid = jsonObj.getString("uuid");
        String name = jsonObj.getString("name");
        if (StringUtils.isBlank(name)) {
            throw new ApiRuntimeException("请选择下载的音频");
        }
        Path path = Paths.get(Config.AUDIO_HOME() + File.separator + uuid + File.separator + name);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new AudioExpiredException(name);
        }
        String fileName = AudioUtil.getEncodedFileName(request.getHeader("User-Agent"), name);
        response.setHeader("Content-Disposition", " attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/octet-stream");
        // 以下两行为aplayer拖拽进度条必需的选项
        response.setHeader("Accept-Ranges", "bytes");
        response.setContentLengthLong(Files.size(path));
        try (ServletOutputStream os = response.getOutputStream(); InputStream is = Files.newInputStream(path);) {
            IOUtils.copy(is, os);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new AudioDownloadException(name);
        }
        return null;
    }

}
