package com.ainoe.audio.constant;

public enum ApiParamType {
    INTEGER("int", "整型"),
    ENUM("enum", "枚举型"),
    BOOLEAN("boolean", "布尔型"),
    STRING("string", "字符型"),
    LONG("long", "长整型"),
    JSONOBJECT("jsonObject", "json对象"),
    JSONARRAY("jsonArray", "json数组"),
    REGEX("regex", "正则表达式"),
    DOUBLE("double", "双精度浮点数"),
    ;

    private final String name;
    private final String text;

    ApiParamType(String _name, String _text) {
        this.name = _name;
        this.text = _text;
    }

    public String getValue() {
        return name;
    }

    public String getText() {
        return text;
    }

}
