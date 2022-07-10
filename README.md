# audio
基于FFmpeg技术的音频格式转换项目

# 技术栈
- springboot
- ffmpeg
- javacv

# 启动命令
> application.properties需放在配置文件目录下

`java -jar -Xbootclasspath/a:配置文件目录路径 {audio.jar所在目录路径}/audio.jar`

以windows为例：

`java -jar -Xbootclasspath/a:D:\document\audio\config D:\document\audio\audio.jar`

# 使用
使用API调试软件调用系统接口（以postman为例）

![img.png](postman.png)

