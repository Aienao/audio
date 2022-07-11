package com.ainoe.audio.api;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.constvalue.ApiParamType;
import com.ainoe.audio.exception.core.ApiRuntimeException;
import com.ainoe.audio.restful.annotation.Description;
import com.ainoe.audio.restful.annotation.Input;
import com.ainoe.audio.restful.annotation.Param;
import com.ainoe.audio.restful.component.RestfulBinaryStreamApiComponentBase;
import com.ainoe.audio.util.AudioUtil;
import com.ainoe.audio.util.ConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class AudioPackDownloadApi extends RestfulBinaryStreamApiComponentBase {

    static Logger logger = LoggerFactory.getLogger(AudioPackDownloadApi.class);

    @Override
    public String getToken() {
        return "/audio/pack/download";
    }

    @Override
    public String getName() {
        return "音频打包下载";
    }

    @Input({
            @Param(name = "nameList", type = ApiParamType.JSONARRAY, isRequired = true, desc = "文件名称列表"),
    })
    @Description(desc = "音频打包下载")
    @Override
    public Object myDoService(JSONObject jsonObj, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ConfigUtil.checkAudioHome();
        List<String> list = jsonObj.getJSONArray("nameList").toJavaList(String.class);
        if (CollectionUtils.isEmpty(list)) {
            throw new ApiRuntimeException("请选择下载的音频");
        }
        List<String> expireAudio = new ArrayList<>();
        String fileName = AudioUtil.getEncodedFileName(request.getHeader("User-Agent"), "audio.zip");
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", " attachment; filename=\"" + fileName + "\"");
        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (String name : list) {
                Path path = Paths.get(Config.AUDIO_HOME() + File.separator + name);
                if (!Files.exists(path) || !Files.isRegularFile(path)) {
                    expireAudio.add(name);
                    continue;
                }
                InputStream is = Files.newInputStream(path);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                IOUtils.copy(is, os);
                zos.putNextEntry(new ZipEntry(name));
                zos.write(os.toByteArray());
                zos.closeEntry();
            }
        }
        if (expireAudio.size() > 0) {
            logger.error("expire audio file:{}", String.join(",", expireAudio));
        }
        return null;
    }

}
