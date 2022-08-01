package com.ainoe.audio.dto;

import com.ainoe.audio.constant.ApiParamType;
import com.ainoe.audio.restful.annotation.EntityField;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApiVo implements Serializable {

    private static final long serialVersionUID = 3689437871016436622L;

    @EntityField(name = "名称", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "处理器", type = ApiParamType.STRING)
    private String handler;
    @EntityField(name = "处理器名", type = ApiParamType.STRING)
    private String handlerName;
    @EntityField(name = "token", type = ApiParamType.STRING)
    private String token;
    @EntityField(name = "描述", type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "接口类型", type = ApiParamType.STRING)
    private String type;
    @JSONField(serialize = false)
    private transient JSONObject pathVariableObj;
    @JSONField(serialize = false)
    private transient List<String> pathVariableList;


    public void addPathVariable(String para) {
        if (pathVariableList == null) {
            pathVariableList = new ArrayList<>();
        }
        pathVariableList.add(para);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public JSONObject getPathVariableObj() {
        return pathVariableObj;
    }

    public void setPathVariableObj(JSONObject pathVariableObj) {
        this.pathVariableObj = pathVariableObj;
    }

    public List<String> getPathVariableList() {
        return pathVariableList;
    }

    public void setPathVariableList(List<String> pathVariableList) {
        this.pathVariableList = pathVariableList;
    }


}
