package com.ainoe.audio.restful.base;

import com.ainoe.audio.dto.ApiVo;
import com.ainoe.audio.exception.core.ApiRuntimeException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.aop.framework.AopContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class ApiComponentBase implements MyApiComponent {

    public final Object doService(ApiVo apiVo, JSONObject paramObj) throws Exception {
        Object result;
        try {
            try {
                Object proxy = AopContext.currentProxy();
                Method method = proxy.getClass().getMethod("myDoService", JSONObject.class);
                result = method.invoke(proxy, paramObj);
            } catch (IllegalStateException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException ex) {
                result = myDoService(paramObj);
            } catch (Exception ex) {
                if (ex.getCause() != null && ex.getCause() instanceof ApiRuntimeException) {
                    throw new ApiRuntimeException(ex.getCause().getMessage());
                } else {
                    throw ex;
                }
            }
        } catch (Exception e) {
            Throwable target = e;
            //如果是反射抛得异常，则需要拆包，把真实得异常类找出来
            while (target instanceof InvocationTargetException) {
                target = ((InvocationTargetException) target).getTargetException();
            }
            throw (Exception) target;
        }
        return result;
    }


}
