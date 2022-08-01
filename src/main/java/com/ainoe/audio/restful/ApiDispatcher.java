package com.ainoe.audio.restful;


import com.ainoe.audio.config.Config;
import com.ainoe.audio.constant.ApiType;
import com.ainoe.audio.dto.ApiHandlerVo;
import com.ainoe.audio.dto.ApiVo;
import com.ainoe.audio.exception.ApiNotFoundException;
import com.ainoe.audio.exception.ComponentNotFoundException;
import com.ainoe.audio.exception.core.ApiRuntimeException;
import com.ainoe.audio.restful.base.IApiComponent;
import com.ainoe.audio.restful.base.IBinaryStreamApiComponent;
import com.ainoe.audio.restful.component.RestfulApiComponentFactory;
import com.ainoe.audio.util.ConfigUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;

@Controller
@RequestMapping("/api/")
public class ApiDispatcher {
    /**
     * 错误码定义
     * 520:已知接口错误，error.log不再打出堆栈信息，只会把错误信息返回给前端
     * 521:不明错误，会在error.log中打出
     **/
    Logger logger = LoggerFactory.getLogger(ApiDispatcher.class);
    private static final String RESPONSE_TYPE_JSON = "application/json;charset=UTF-8";

    private JSON doIt(HttpServletRequest request, HttpServletResponse response, String token, ApiType apiType, JSONObject paramObj) throws Exception {
        setUserUuid(request, response, paramObj);
        ApiVo interfaceVo = RestfulApiComponentFactory.getApiByToken(token);
        if (interfaceVo == null) {
            throw new ApiNotFoundException("token为“" + token + "”的接口不存在或已被禁用");
        } else if (interfaceVo.getPathVariableObj() != null) {
            // 融合路径参数
            paramObj.putAll(interfaceVo.getPathVariableObj());
        }
        ApiHandlerVo apiHandlerVo = RestfulApiComponentFactory.getApiHandlerByHandler(interfaceVo.getHandler());
        if (apiHandlerVo == null) {
            throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
        }
        if (apiType.equals(ApiType.OBJECT)) {
            IApiComponent restComponent = RestfulApiComponentFactory.getInstance(interfaceVo.getHandler());
            if (restComponent != null) {
                Long startTime = System.currentTimeMillis();
                Object returnV = restComponent.doService(interfaceVo, paramObj);
                return getResult(startTime, returnV, restComponent.isRaw());
            } else {
                throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
            }
        } else if (apiType.equals(ApiType.BINARY)) {
            IBinaryStreamApiComponent restComponent = RestfulApiComponentFactory.getBinaryInstance(interfaceVo.getHandler());
            if (restComponent != null) {
                Long starTime = System.currentTimeMillis();
                Object returnV = restComponent.doService(interfaceVo, paramObj, request, response);
                return getResult(starTime, returnV, restComponent.isRaw());
            } else {
                throw new ComponentNotFoundException("接口组件:" + interfaceVo.getHandler() + "不存在");
            }
        }
        return new JSONObject();
    }

    private void setUserUuid(HttpServletRequest request, HttpServletResponse response, JSONObject paramObj) throws IOException {
        // 先从cookie中读取
        Cookie[] cookies = request.getCookies();
        String uuid = null;
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("uuid")) {
                    uuid = cookie.getValue();
                    paramObj.put("uuid", uuid);
                    break;
                }
            }
        }
        // cookie中没有就从参数读取
        if (StringUtils.isBlank(uuid)) {
            JSONObject parameters = getParameters(request);
            uuid = parameters.getString("uuid");
        }
        // 参数中还没有就说明是新用户
        if (StringUtils.isBlank(uuid)) {
            uuid = UUID.randomUUID().toString().replace("-", "");
            paramObj.put("uuid", uuid);
            Cookie cookie = new Cookie("uuid", uuid);
            cookie.setMaxAge(Config.USER_UUID_MAX_AGE());
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        ConfigUtil.checkAudioHome();
        ConfigUtil.createUserAudioHome(uuid);
    }

    @RequestMapping(value = "/rest/**", method = RequestMethod.GET)
    public void dispatcherForGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject paramObj = getParameters(request);
        doRequest(request, response, paramObj, ApiType.OBJECT);
    }

    @RequestMapping(value = "/rest/**", method = RequestMethod.POST, consumes = "application/json")
    public void dispatcherForPost(@RequestBody JSONObject json, HttpServletRequest request, HttpServletResponse response) throws Exception {
        doRequest(request, response, json, ApiType.OBJECT);
    }

    @RequestMapping(value = "/binary/**", method = RequestMethod.GET)
    public void dispatcherForPostBinary(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject paramObj = getParameters(request);
        doRequest(request, response, paramObj, ApiType.BINARY);
    }

    @RequestMapping(value = "/binary/**", method = RequestMethod.POST, consumes = "application/json")
    public void dispatcherForPostBinaryMultipart(@RequestBody JSONObject json, HttpServletRequest request, HttpServletResponse response) throws IOException {
        doRequest(request, response, json, ApiType.BINARY);
    }

    @RequestMapping(value = "/binary/**", method = RequestMethod.POST, consumes = "multipart/form-data")
    public void dispatcherForPostBinaryMultipart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject paramObj = getParameters(request);
        doRequest(request, response, paramObj, ApiType.BINARY);
    }

    private String getToken(HttpServletRequest request) {
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return new AntPathMatcher().extractPathWithinPattern(pattern, request.getServletPath());
    }

    private JSONObject getParameters(HttpServletRequest request) {
        JSONObject paramObj = new JSONObject();
        Enumeration<String> paraNames = request.getParameterNames();
        while (paraNames.hasMoreElements()) {
            String p = paraNames.nextElement();
            String[] vs = request.getParameterValues(p);
            if (vs.length > 1) {
                paramObj.put(p, vs);
            } else {
                paramObj.put(p, request.getParameter(p));
            }
        }
        return paramObj;
    }

    private JSON getResult(Long starTime, Object returnV, boolean raw) {
        Long endTime = System.currentTimeMillis();
        if (!raw) {
            JSONObject returnObj = new JSONObject();
            returnObj.put("TimeCost", endTime - starTime);
            returnObj.put("Return", returnV);
            returnObj.put("Status", "OK");
            return returnObj;
        } else {
            Object o = JSON.parse(JSON.toJSONString(returnV));
            if (o instanceof JSONObject) {
                return (JSONObject) o;
            } else {
                return (JSONArray) o;
            }
        }
    }

    private void doRequest(HttpServletRequest request, HttpServletResponse response, JSONObject paramObj, ApiType apiType) throws IOException {
        String token = getToken(request);
        JSON returnObj;
        try {
            returnObj = doIt(request, response, token, apiType, paramObj != null ? paramObj : new JSONObject());
        } catch (ApiRuntimeException ex) {
            response.setStatus(520);
            JSONObject rObj = new JSONObject();
            rObj.put("Status", "ERROR");
            rObj.put("Message", ex.getMessage());
            returnObj = rObj;
        } catch (Exception ex) {
            response.setStatus(521);
            JSONObject rObj = new JSONObject();
            rObj.put("Status", "ERROR");
            rObj.put("Message", ExceptionUtils.getStackTrace(ex));
            returnObj = rObj;
            logger.error(ex.getMessage(), ex);
        }
        if (!response.isCommitted()) {
            response.setContentType(RESPONSE_TYPE_JSON);
            response.getWriter().print(returnObj);
        }
    }

}
