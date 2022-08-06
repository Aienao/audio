# AudioCoder

基于FFmpeg的音频格式转换工具

## 功能

- 支持大部分音频格式输入，目前支持FLAC、MP3（支持比特率选择）输出
- 多线程批量转换
- 支持多用户
- 支持自动清理历史转换数据
- 支持音频元数据读取

## 技术栈

### 后端

- ffmpeg
- java8
- springboot
- javacv
- jaudiotagger

### 前端

- vue3
- vuex
- axios
- element-plus
- aplayer

## 安装

### 安装FFmpeg

本项目依赖FFmpeg，故需要先行安装FFmpeg，安装请参考官方指南

### 启动项目

> application.properties需要与jar包位于同一目录下

`java -jar -Xbootclasspath/a:配置文件目录路径 {audio.jar所在目录路径}/audio.jar`

以Linux为例：

`java -jar -Xbootclasspath/a:/home/audio/audio.jar`

