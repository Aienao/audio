package com.ainoe.audio.dto;


import com.ainoe.audio.constant.ApiParamType;
import com.ainoe.audio.restful.annotation.EntityField;

public class ApiHandlerVo {
    @EntityField(name = "处理器", type = ApiParamType.STRING)
    private String handler;
    @EntityField(name = "名称", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "处理器类型", type = ApiParamType.STRING)
    private String type;

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
