package com.github.sunflowerlb.framework.web.servlet.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import com.github.sunflowerlb.framework.core.exception.ApplicationException;
import com.github.sunflowerlb.framework.core.log.Log;
import com.github.sunflowerlb.framework.core.log.LogOp;
import com.github.sunflowerlb.framework.web.form.FormToken;
import com.github.sunflowerlb.framework.web.form.TokenConst;
import com.github.sunflowerlb.framework.web.form.TokenManager;
import com.github.sunflowerlb.framework.web.servlet.json.JsonMessage;
import com.github.sunflowerlb.framework.web.servlet.json.WebJsonUtil;

/**
 * 基于信息源的异常解析器
 * <p>
 * 指定错误页面、错误码及错误信息，由解析器来进行处理输出
 * 
 * @author lb
 */
public class IHomeHandlerExceptionResolver extends AbstractIHomeHandlerExceptionResolver {

    private static final Logger logger = LoggerFactory.getLogger(IHomeHandlerExceptionResolver.class);

    private static String DEFAULT_ERROR_VIEW_NAME = "error";

    private static final String ERROR_CODE_ATTR_NAME = "code";

    private static final String ERROR_MESSAGE_ATTR_NAME = "message";

    private static final String DEFAULT_ERROR_CODE = "500";

    private static final String DEFAULT_ERROR_MESSAGE = "系统错误";

    /**
     * -----------------------------------------------------
     */

    /**
     * 默认的错误的view名称
     */
    private String errorViewName = DEFAULT_ERROR_VIEW_NAME;

    private String defaultErrorCode = DEFAULT_ERROR_CODE;

    private String defaultErrorMessage = DEFAULT_ERROR_MESSAGE;

    private MessageSource messageSource;
    
    private TokenManager formTokenManager;

    /**
     * 默认处理所有异常,如果要有不同行为的话,可以在子类扩展
     */
    @Override
    protected boolean support(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return true;
    }

    /**
     * 记录异常信息
     */
    @Override
    protected void recordException(Exception ex, HttpServletRequest request) {
        logger.error(Log.op(LogOp.EXP_RESOLVER_DEAL).msg("deal url fail").kv("url", request.getRequestURI()).toString(), ex);
    }

    /**
     * 处理非Json的请求,返回默认的viewName
     */
    @Override
    protected ModelAndView resolveExceptionForNonJson(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView view = new ModelAndView();
        view.setViewName(errorViewName);

        String errorCode = getErrorCode(request, ex);
        String errorMessage = getErrorMessage(errorCode, request, ex);

        view.addObject(ERROR_CODE_ATTR_NAME, errorCode);
        view.addObject(ERROR_MESSAGE_ATTR_NAME, errorMessage);

        // 生成防重复提交的token
        if(generateToken(handler)) {
            view.addObject(TokenConst.TOKEN, formTokenManager.newToken());
        }
        return view;
    }

    /**
     * 处理Json请求
     */
    protected ModelAndView resolveExceptionForJson(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        response.setContentType(WebJsonUtil.JSON_CONTENT_TYPE);

        JsonMessage jsonMessage = createJsonMessage(request, ex);
        jsonMessage = decorateJsonMessage(jsonMessage, request, ex);

        // 生成防重复提交的token
        if(generateToken(handler)) {
            jsonMessage.setToken(formTokenManager.newToken());
        }
        
        String text = WebJsonUtil.toJSONPString(jsonMessage);
        byte[] bytes = text.getBytes(UTF8);

        try {
            OutputStream out = response.getOutputStream();
            out.write(bytes);
            out.flush();
            return new ModelAndView();
        } catch (IOException e) {
            throw new RuntimeException("resolveExceptionForJson Fail", e);
        }
    }

    protected JsonMessage createJsonMessage(HttpServletRequest request, Exception ex) {
        JsonMessage jsonMessage = new JsonMessage();
        String errorCode = getErrorCode(request, ex);
        String errorMessage = getErrorMessage(errorCode, request, ex);
        jsonMessage.setCode(errorCode).setMessage(errorMessage);
        return jsonMessage;
    }

    /**
     * 获取错误码
     * 
     * @param request
     * @param ex
     * @return
     */
    protected String getErrorCode(HttpServletRequest request, Exception ex) {
        if (ex instanceof ApplicationException) {
            int code = ((ApplicationException) ex).getCode();
            // 如果code为0，则使用默认的错误码
            if (code == 0) {
                return defaultErrorCode;
            } else {
                return String.valueOf(code);
            }
        }
        return defaultErrorCode;
    }

    /**
     * 获取错误消息
     * 
     * @param errorCode
     * @param request
     * @param ex
     * @return
     */
    protected String getErrorMessage(String errorCode, HttpServletRequest request, Exception ex) {
    	String retMessage = null;
        if (ex instanceof ApplicationException) {
            if (messageSource == null) {
               // 应用异常且不配置messageSource
            	if(StringUtils.isNotBlank(ex.getMessage())){
            		retMessage = ex.getMessage(); 
            	}else {
            		if(ex.getCause() != null){
            			retMessage = ex.getCause().getMessage();  
            		}else {
            			retMessage = defaultErrorMessage; 
	            		logger.error(Log.op(LogOp.EXP_RESOLVER_FAIL).msg(ex.getClass().getSimpleName()+" no message").kv("code", errorCode).toString(), ex);
            		}
            	}
            } else {
                // 应用异常且配置了messageSource
                ApplicationException ae = (ApplicationException) ex;
                
                Class<?> clz = ae.getClass();
                String msgKey = errorCode;
                
                // 直接用errorCode去取
                retMessage = getMessageFromSource(msgKey, ae);
                // 用"com.ihome.framework.usage.exception.FrameworkUsageException.CustomerException.errorCode"的形式去取
                if (retMessage == null) {
                    msgKey = clz.getName() + "." + errorCode;
                    retMessage = getMessageFromSource(msgKey, ae);
                }
                // 用"CustomerException.errorCode"的形式去取
                if(retMessage == null) {
                    msgKey = clz.getSimpleName() + "." + errorCode;
                    retMessage = getMessageFromSource(msgKey, ae);
                }
                // 配置文件中没有的,就直接采用原来的错误信息
                if(retMessage == null) {
                    retMessage = ae.getMessage();
                }
            }
        } else {
            retMessage = defaultErrorMessage;
        }
        return retMessage;
    }
    
    private String getMessageFromSource(String errorCode, ApplicationException ae) {
        String retMessage = null;
        try {
            retMessage = messageSource.getMessage(errorCode, ae.getArgs(), LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException nmex) {
            logger.warn(Log.op(LogOp.EXP_RESOLVER_FAIL).msg("errorCode not in messageSource").kv("code", errorCode).toString());
        }        
        return retMessage;
    }
    
    /**
     * 判断是否需要生成防重复提交的token
     * @param handler
     * @return
     */
    private boolean generateToken(Object handler) {
        if(formTokenManager == null) {
            return false;
        }
        if(handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            Method method = hm.getMethod();
            FormToken formToken = method.getAnnotation(FormToken.class);
            if(formToken == null) {
                return false;
            }
            if(formToken.generateToken()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @param errorViewName
     *            the errorViewName to set
     */
    public void setErrorViewName(String errorViewName) {
        this.errorViewName = errorViewName;
    }

    /**
     * @param defaultErrorCode
     *            the defaultErrorCode to set
     */
    public void setDefaultErrorCode(String defaultErrorCode) {
        this.defaultErrorCode = defaultErrorCode;
    }

    /**
     * @param defaultErrorMessage
     *            the defaultErrorMessage to set
     */
    public void setDefaultErrorMessage(String defaultErrorMessage) {
        this.defaultErrorMessage = defaultErrorMessage;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    public TokenManager getFormTokenManager() {
        return formTokenManager;
    }

    public void setFormTokenManager(TokenManager formTokenManager) {
        this.formTokenManager = formTokenManager;
    }
}
