package com.ainoe.audio.constvalue;

public enum ApiType {
    OBJECT("object", "对象模式", "rest/"),
    BINARY("binary", "字节流模式", "binary/");

    private final String name;
    private final String text;
    private final String urlPre;

    ApiType(String _name, String _text, String _urlPre) {
        this.name = _name;
        this.text = _text;
        this.urlPre = _urlPre;
    }

    public String getValue() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getUrlPre() {
        return urlPre;
    }

    public static String getText(String name) {
        for (ApiType s : values()) {
            if (s.getValue().equals(name)) {
                return s.getText();
            }
        }
        return null;
    }

    public static String getUrlPre(String name) {
        for (ApiType s : values()) {
            if (s.getValue().equals(name)) {
                return s.getUrlPre();
            }
        }
        return null;
    }
}
