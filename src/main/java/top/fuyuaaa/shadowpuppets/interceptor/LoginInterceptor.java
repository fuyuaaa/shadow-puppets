package top.fuyuaaa.shadowpuppets.interceptor;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import top.fuyuaaa.shadowpuppets.holder.LoginUserHolder;
import top.fuyuaaa.shadowpuppets.model.LoginUserInfo;
import top.fuyuaaa.shadowpuppets.model.bo.UserBO;
import top.fuyuaaa.shadowpuppets.service.UserService;
import top.fuyuaaa.shadowpuppets.util.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author: fuyuaaa
 * @creat: 2019-03-29 10:21
 */
@Slf4j
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    private final static String UN_LOG_METHOD_NAME = "checkOrderPaidAndUpdateOrderStatus";

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    UserService userService;
    @Value("${ignore-all-security}")
    boolean isIgnoreAllSecurity;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        String requestPath = request.getRequestURI();
        String token = request.getHeader("ACCESS_TOKEN");
        //这个请求过于频繁，不输出日志
        if (!UN_LOG_METHOD_NAME.equals(method.getName())) {
            log.info("==请求信息== requestPath:{}, Method:{}, ACCESS_TOKEN:{}", requestPath, method.getName(), token);
        }
        //是有效的token，设置user
        if (!isInvalidToken(token)) {
            setUser(token);
        }
        return true;
    }

    private boolean isInvalidToken(String token) {
        return StringUtils.isEmpty(token) || null == redisTemplate.hasKey(token) || !redisTemplate.hasKey(token);
    }

    private boolean setUser(String token) {
        String tel = token.split(":")[1];
        UserBO userBO = userService.getByTel(tel);
        if (null == userBO || null == userBO.getId()) {
            return false;
        }
        LoginUserInfo loginUserInfo = BeanUtils.copyProperties(userBO, LoginUserInfo.class);
        LoginUserHolder.instance().put(loginUserInfo);
        return true;
    }
}
