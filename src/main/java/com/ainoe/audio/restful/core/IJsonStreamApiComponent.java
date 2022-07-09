package com.ainoe.audio.restful.core;

import com.ainoe.audio.dto.ApiVo;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

public interface IJsonStreamApiComponent {

    String getId();

    String getName();


    // true时返回格式不再包裹固定格式
    default boolean isRaw() {
        return false;
    }

    Object doService(ApiVo interfaceVo, JSONObject paramObj, JSONReader jsonReader) throws Exception;

    JSONObject help();
}
