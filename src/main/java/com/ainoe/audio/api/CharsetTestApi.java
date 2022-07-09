package com.ainoe.audio.api;

import com.ainoe.audio.constvalue.ApiParamType;
import com.ainoe.audio.restful.annotation.Description;
import com.ainoe.audio.restful.annotation.Input;
import com.ainoe.audio.restful.annotation.Output;
import com.ainoe.audio.restful.annotation.Param;
import com.ainoe.audio.restful.core.privateapi.PrivateApiComponentBase;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class CharsetTestApi extends PrivateApiComponentBase {

    static Logger logger = LoggerFactory.getLogger(CharsetTestApi.class);

    @Override
    public String getToken() {
        return "/encoding/test";
    }

    @Override
    public String getName() {
        return "test";
    }

    @Input({
            @Param(name = "encoding", type = ApiParamType.STRING, desc = "字符集", isRequired = true)
    })
    @Output({})
    @Description(desc = "test")
    @Override
    public Object myDoService(JSONObject jsonObj) throws IOException {
        List<String> list = new ArrayList<>();
        String encoding = jsonObj.getString("encoding");
        File file = new File("C:\\Users\\Aieano\\Desktop\\charsetTest.txt");
        if (file.exists() && file.isFile()) {
            String content;
            try (RandomAccessFile fis = new RandomAccessFile(file, "r")) {
                while ((content = fis.readLine()) != null) {
                    String newContent = new String(content.getBytes(StandardCharsets.ISO_8859_1), encoding);
                    list.add(newContent);
                    System.out.println(newContent);
                }
            }
        }
        return list;
    }

    public static void main(String[] args) {
        File file = new File("C:\\Users\\Aieano\\Desktop\\charsetTest.txt");
        if (file.exists() && file.isFile()) {
            String content;
            try (RandomAccessFile fis = new RandomAccessFile(file, "r")) {
                while ((content = fis.readLine()) != null) {
                    System.out.println(new String(content.getBytes(StandardCharsets.ISO_8859_1), "GBK"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
