package com.ainoe.audio.dto;

import com.ainoe.audio.constant.ApiParamType;
import com.ainoe.audio.restful.annotation.EntityField;

public class AudioVo {
    @EntityField(name = "名称", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "格式", type = ApiParamType.STRING)
    private String format;
    @EntityField(name = "采样率", type = ApiParamType.INTEGER)
    private Integer sampleRate;
    @EntityField(name = "比特率", type = ApiParamType.LONG)
    private Long bitRate; // 原始值，换算为Kbs的公式：bitRate / 1000
    @EntityField(name = "声道数", type = ApiParamType.INTEGER)
    private Integer channels;
    @EntityField(name = "时长", type = ApiParamType.STRING)
    private String duration;
    @EntityField(name = "发行日期", type = ApiParamType.STRING)
    private String date;
    @EntityField(name = "作者", type = ApiParamType.STRING)
    private String artist;
    @EntityField(name = "专辑", type = ApiParamType.STRING)
    private String album;
    @EntityField(name = "标题", type = ApiParamType.STRING)
    private String title;
    @EntityField(name = "专辑封面（base64编码后的字符串）", type = ApiParamType.STRING)
    private String cover;
    @EntityField(name = "文件大小", type = ApiParamType.LONG)
    private Long size; // 单位为字节，换算为MB的公式：size / 1024 / 1024

    public AudioVo() {
    }

    public AudioVo(String name, String format, Integer sampleRate, Long bitRate, Integer channels, String duration, String date, String artist, String album, String title, Long size, String cover) {
        this.name = name;
        this.format = format;
        this.sampleRate = sampleRate;
        this.bitRate = bitRate;
        this.channels = channels;
        this.duration = duration;
        this.date = date;
        this.artist = artist;
        this.album = album;
        this.title = title;
        this.size = size;
        this.cover = cover;
    }

    public AudioVo(String name, String format, Integer sampleRate, Long bitRate, Integer channels, String duration, String date, String artist, String album, String title, Long size) {
        this.name = name;
        this.format = format;
        this.sampleRate = sampleRate;
        this.bitRate = bitRate;
        this.channels = channels;
        this.duration = duration;
        this.date = date;
        this.artist = artist;
        this.album = album;
        this.title = title;
        this.size = size;
    }

    public AudioVo(String name, String format, Long bitRate, String duration, String date, String artist, String album, String title, String cover) {
        this.name = name;
        this.format = format;
        this.bitRate = bitRate;
        this.duration = duration;
        this.date = date;
        this.artist = artist;
        this.album = album;
        this.title = title;
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public Long getBitRate() {
        return bitRate;
    }

    public void setBitRate(Long bitRate) {
        this.bitRate = bitRate;
    }

    public Integer getChannels() {
        return channels;
    }

    public void setChannels(Integer channels) {
        this.channels = channels;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
