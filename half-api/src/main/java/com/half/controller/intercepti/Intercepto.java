package com.half.controller.intercepti;

import com.half.utils.JsonUtils;
import com.half.utils.LexJSONResult;
import com.half.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class Intercepto implements HandlerInterceptor {
    /**
     * 拦截请求 在controller之前
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @return
     * @throws Exception
     */
    @Autowired
    private RedisOperator redisOperator;
    public final String USERSESSIONID="user-session-id";

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        String userId = httpServletRequest.getHeader("userId");
        String userToken=httpServletRequest.getHeader("userToken");
        if (!StringUtils.isEmpty(userId)&& !StringUtils.isEmpty(userToken)){
//            System.out.println("不为空");
            String token = redisOperator.get(USERSESSIONID + ":" + userId);
            if (StringUtils.isEmpty(token)){
                returnErrorResponse(httpServletResponse,new LexJSONResult().errorTokenMsg("用户信息已经过期，请重新登录"));
                System.out.println("用户信息已经过期，请重新登录");
                return false;
            }
            else if (!token.equals(userToken)){
                returnErrorResponse(httpServletResponse,new LexJSONResult().errorTokenMsg("该账号已经登录"));
                System.out.println("该账号已经登录");
                return false;
            }else {
                return true;
            }
        }else {
            returnErrorResponse(httpServletResponse,new LexJSONResult().errorTokenMsg("你还没有登录，请登录"));
            System.out.println("请登录");
            return false;
        }
    }


    public void returnErrorResponse(HttpServletResponse response, LexJSONResult result)
            throws IOException, UnsupportedEncodingException {
        OutputStream out=null;
        try{
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally{
            if(out!=null){
                out.close();
            }
        }
    }

    /**
     * 请求controller后 页面渲染前
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 最后
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
