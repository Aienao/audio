package com.ainoe.audio.restful.base;

import com.ainoe.audio.dto.ApiVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.ClassUtils;

public interface IApiComponent {

    default String getClassName() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

    default boolean isRaw() {
        return false;
    }

    String getName();

    Object doService(ApiVo apiVo, JSONObject jsonObj) throws Exception;

}
