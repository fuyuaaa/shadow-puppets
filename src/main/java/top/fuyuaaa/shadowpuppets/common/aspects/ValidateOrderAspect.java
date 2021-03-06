package top.fuyuaaa.shadowpuppets.common.aspects;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.fuyuaaa.shadowpuppets.common.enums.ExEnum;
import top.fuyuaaa.shadowpuppets.dao.CourseOrderDao;
import top.fuyuaaa.shadowpuppets.common.exceptions.OrderOwnerException;
import top.fuyuaaa.shadowpuppets.common.exceptions.ParamException;
import top.fuyuaaa.shadowpuppets.common.holders.LoginUserHolder;
import top.fuyuaaa.shadowpuppets.model.LoginUserInfo;
import top.fuyuaaa.shadowpuppets.model.bo.GoodsOrderBO;
import top.fuyuaaa.shadowpuppets.model.po.CourseOrderPO;
import top.fuyuaaa.shadowpuppets.service.GoodsOrderService;

import javax.servlet.http.HttpServletRequest;


/**
 * 校验订单是否属于用户本人
 *
 * @author fuyuaaa
 * @creat: 2019-04-11 22:44
 */
@AllArgsConstructor
@Slf4j
@Aspect
@Component
public class ValidateOrderAspect {


    @Autowired
    GoodsOrderService goodsOrderService;
    @Autowired
    CourseOrderDao courseOrderDao;

    @Pointcut("@annotation(top.fuyuaaa.shadowpuppets.common.annotations.ValidateOrderOwner)")
    public void pointCutValidateOrderOwner() {
    }

    @SuppressWarnings("all")
    @Before("pointCutValidateOrderOwner()")
    public void checkOrderBelongUser(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String orderId = request.getParameter("orderId");
        if (StringUtils.isEmpty(orderId)) {
            throw new ParamException(ExEnum.PARAM_ERROR.getMsg());
        }
        GoodsOrderBO goodsOrderBO = goodsOrderService.getById(orderId);
        LoginUserInfo loginUserInfo = LoginUserHolder.instance().get();
        if (!goodsOrderBO.getUserId().equals(loginUserInfo.getId())) {
            throw new OrderOwnerException(ExEnum.IS_NOT_ORDER_OWNER.getMsg());
        }
    }

    @Pointcut("@annotation(top.fuyuaaa.shadowpuppets.common.annotations.ValidateCourseOrderOwner)")
    public void pointCutValidateCourseOrderOwner() {
    }

    @SuppressWarnings("all")
    @Before("pointCutValidateCourseOrderOwner()")
    public void checkCourseOrderBelongUser(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String orderId = request.getParameter("orderId");
        if (StringUtils.isEmpty(orderId)) {
            throw new ParamException(ExEnum.PARAM_ERROR.getMsg());
        }
        CourseOrderPO courseOrderPO = courseOrderDao.getById(orderId);
        LoginUserInfo loginUserInfo = LoginUserHolder.instance().get();
        if (!courseOrderPO.getUserId().equals(loginUserInfo.getId())) {
            throw new OrderOwnerException(ExEnum.IS_NOT_ORDER_OWNER.getMsg());
        }
    }
}
