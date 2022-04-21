package com.sin.intercept;

import com.sin.util.HttpJSONResult;
import com.sin.util.JsonUtils;
import com.sin.util.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.apache.el.parser.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Intercept要生效 是需要在java中进行配置的
 */
public class UserTokenIntercept implements HandlerInterceptor {
    private final Logger logger = LoggerFactory.getLogger(UserTokenIntercept.class);

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 在访问Controller之后需要做的
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // headerUserId
        // headerUserToken
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");
        logger.info("User: id: " + userId + ", token: " + userToken);
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String uniqueUserToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
            if (StringUtils.isBlank(uniqueUserToken) && !uniqueUserToken.equals(userToken)) {
                logger.info("Anonymous man from " + request.getRemoteAddr() + " do not match the token in the redis");
                returnErrorResponse(response, HttpJSONResult.errorMsg("Error in the internal!"));
                return false;
            }
            return true;
        } else {
            logger.info(request.getRemoteAddr() + ", Do not login!");
            returnErrorResponse(response, HttpJSONResult.errorMsg("Please login!"));
            return false;
        }
    }

    public void returnErrorResponse(HttpServletResponse response,
                                    HttpJSONResult result) {
        OutputStream out = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 访问controller之后 渲染视图之前
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * controller之后 渲染视图之后
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
