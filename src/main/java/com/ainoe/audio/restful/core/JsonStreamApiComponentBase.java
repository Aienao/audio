package com.ainoe.audio.restful.core;

import com.ainoe.audio.dto.ApiVo;
import com.ainoe.audio.exception.core.ApiRuntimeException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

public abstract class JsonStreamApiComponentBase implements MyJsonStreamApiComponent {

    @Override
    public final Object doService(ApiVo apiVo, JSONObject paramObj, JSONReader jsonReader) throws Exception {
        Object result;
        try {
            Object proxy = AopContext.currentProxy();
            Class<?> targetClass = AopUtils.getTargetClass(proxy);
            Method method = proxy.getClass().getMethod("myDoService", JSONObject.class, JSONReader.class);
            result = method.invoke(proxy, paramObj, jsonReader);
        } catch (IllegalStateException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException ex) {
            result = myDoService(paramObj, jsonReader);
        } catch (Exception ex) {
            if (ex.getCause() != null && ex.getCause() instanceof ApiRuntimeException) {
                throw new ApiRuntimeException(ex.getCause().getMessage());
            } else {
                throw ex;
            }
        }
        return result;
    }

    public final String getId() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

}
