package com.github.sunflowerlb.framework.web.form;

import java.lang.reflect.Method;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.sunflowerlb.framework.core.commons.OpResponse;
import com.github.sunflowerlb.framework.core.log.Log;
import com.github.sunflowerlb.framework.core.log.LogOp;
import com.github.sunflowerlb.framework.web.exception.WebErrors;
import com.github.sunflowerlb.framework.web.servlet.json.JsonMessage;

@Aspect
public class FormTokenAspect {

    @Resource
    TokenManager formTokenManager;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    HttpServletRequest request;

    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        // 获取token
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        Method method = ms.getMethod();
        FormToken formToken = method.getAnnotation(FormToken.class);
        if (formToken == null) {
            return pjp.proceed();
        }
        // 检验token
        if (formToken.checkToken()) {
            String token = request.getParameter(TokenConst.TOKEN);
            logger.info(Log.op(LogOp.TOKEN_CHECK).msg("start to check").kv("token", token).toString());
            if (StringUtils.isBlank(token)) {
                throw WebErrors.TOKEN_EMPTY.exp();
            }
            boolean checked = formTokenManager.checkAndDelToken(token);
            if (!checked) {
                throw WebErrors.TOKEN_NOT_EXIST.exp();
            }
            logger.info(Log.op(LogOp.TOKEN_CHECK).msg("token exist").kv("token", token).toString());
            // 如果只配置了checkToken，但是没配置generateToken，如果返回的是业务异常的话，也需要重新返回token
            // 跟直接配置@FormToken(checkToken=true, generateToken=true)相比，会少生成一些token，节省nkv内存
            if(!formToken.generateToken()) {
                return processNewToken(pjp);
            }
        }
        // 设置token
        if(formToken.generateToken()) {
            return processNewToken(pjp);
        }
        return pjp.proceed();
    }
    
    /**
     * 生成新的token
     * @param pjp
     * @param checkCode 是否判断返回码，false则不判断直接生成token，true则判断到为错误码的时候才生成token
     * @return
     * @throws Throwable
     */
    private Object processNewToken(ProceedingJoinPoint pjp) throws Throwable {
    	Object retValue = pjp.proceed();
        String token = formTokenManager.newToken();
        logger.info(Log.op(LogOp.TOKEN_GEN).kv("token", token).toString());
        if (retValue instanceof OpResponse) {
            OpResponse op = (OpResponse) retValue;
            op.setToken(token);
        } else if (retValue instanceof JsonMessage) {
            JsonMessage jsonMessage = (JsonMessage) retValue;
            jsonMessage.setToken(token);
        } else {
            // 如果是返回到页面，则把token放到request.attribute中
            request.setAttribute(TokenConst.TOKEN, token);
        }
        return retValue;
    }
    
    public void setFormTokenManager(TokenManager formTokenManager) {
        this.formTokenManager = formTokenManager;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
    
}
