package com.ainoe.audio.api;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.constvalue.ApiParamType;
import com.ainoe.audio.restful.annotation.Description;
import com.ainoe.audio.restful.annotation.Input;
import com.ainoe.audio.restful.annotation.Param;
import com.ainoe.audio.restful.component.RestfulApiComponentBase;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class AudioDeleteApi extends RestfulApiComponentBase {

    static Logger logger = LoggerFactory.getLogger(AudioDeleteApi.class);

    @Override
    public String getToken() {
        return "/audio/delete";
    }

    @Override
    public String getName() {
        return "删除音频";
    }

    @Input({
            @Param(name = "nameList", type = ApiParamType.JSONARRAY, desc = "文件名称列表"),
            @Param(name = "deleteAll", rule = "0,1", type = ApiParamType.ENUM, desc = "是否删除全部"),
    })
    @Description(desc = "删除音频")
    @Override
    public Object myDoService(JSONObject jsonObj) throws IOException {
        String uuid = jsonObj.getString("uuid");
        JSONArray nameList = jsonObj.getJSONArray("nameList");
        Integer deleteAll = jsonObj.getInteger("deleteAll");
        List<String> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(nameList)) {
            deleteAll = 1;
        } else {
            list = nameList.toJavaList(String.class);
        }
        Path path = Paths.get(Config.AUDIO_HOME() + File.separator + uuid);
        List<String> finalList = list;
        Integer finalDeleteAll = deleteAll;
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (Files.isRegularFile(file)) {
                    if (finalList.contains(file.toFile().getName()) || Objects.equals(finalDeleteAll, 1)) {
                        Files.delete(file);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return null;
    }

}
