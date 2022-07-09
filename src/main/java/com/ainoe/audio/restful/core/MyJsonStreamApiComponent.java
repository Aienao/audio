package com.ainoe.audio.restful.core;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;

public interface MyJsonStreamApiComponent extends IJsonStreamApiComponent {
    Object myDoService(JSONObject paramObj, JSONReader jsonReader) throws Exception;
}
