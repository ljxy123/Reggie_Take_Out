package com.lijun.interceptor;

import com.alibaba.fastjson.JSON;
import com.lijun.common.BaseContext;
import com.lijun.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("访问路径:{}",request.getRequestURI());

        //客户端是否登录的判断
        if (request.getSession().getAttribute("userId")!=null){
            log.info("成功访问的url--->{}",request.getRequestURI());
            log.info("session--->{}",request.getSession().getAttribute("userId"));

            //将登录者的id保存到Threadlocal当中
            Long userId = (Long) request.getSession().getAttribute("userId");
            BaseContext.setCurrentId(userId);

            return true;
        }

        //移动端是否登录的判断
        if (request.getSession().getAttribute("user")!=null){
            log.info("成功访问的url--->{}",request.getRequestURI());
            log.info("用户登录成功的id--->{}",request.getSession().getAttribute("user"));

            //将登录者的id保存到Threadlocal当中
            Long user = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(user);

            return true;
        }

        //发送访问失败的响应数据
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));

        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
