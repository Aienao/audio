package com.ainoe.audio.api;

import com.ainoe.audio.config.Config;
import com.ainoe.audio.restful.annotation.Description;
import com.ainoe.audio.restful.component.RestfulApiComponentBase;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ConfigGetApi extends RestfulApiComponentBase {

    static Logger logger = LoggerFactory.getLogger(ConfigGetApi.class);

    @Override
    public String getToken() {
        return "/config/get";
    }

    @Override
    public String getName() {
        return "获取配置";
    }

    @Description(desc = "获取配置")
    @Override
    public Object myDoService(JSONObject jsonObj) throws IOException {
        return new JSONObject() {
            {
                this.put("autoClean", Config.AUDIO_AUDIO_CLEAN());
                this.put("retainDays", Config.AUDIO_RETAIN_DAYS());
            }
        };
    }

}
