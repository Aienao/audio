package com.ainoe.audio.constvalue;

public enum AuthenticateType {
    NOAUTH("", "无需认证"), BASIC("basic", "Basic认证"), BEARER("bearertoken", "Bearer Token");
    private final String type;
    private final String text;

    AuthenticateType(String _type, String _text) {
        this.type = _type;
        this.text = _text;
    }

    public String getValue() {
        return this.type;
    }

    public String getText() {
        return this.text;
    }
}
