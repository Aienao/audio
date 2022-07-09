package com.ainoe.audio.restful.core;

import com.alibaba.fastjson.JSONObject;

public interface MyApiComponent extends IApiComponent {
    Object myDoService(JSONObject jsonObj) throws Exception;
}
