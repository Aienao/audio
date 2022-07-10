package com.ainoe.audio.restful.base;

import com.alibaba.fastjson.JSONObject;

public interface MyApiComponent extends IApiComponent {
    Object myDoService(JSONObject jsonObj) throws Exception;
}
