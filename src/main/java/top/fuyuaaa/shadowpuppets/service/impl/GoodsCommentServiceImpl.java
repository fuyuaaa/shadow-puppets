package top.fuyuaaa.shadowpuppets.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.fuyuaaa.shadowpuppets.common.annotations.NeedLogin;
import top.fuyuaaa.shadowpuppets.common.enums.ExEnum;
import top.fuyuaaa.shadowpuppets.common.enums.OrderStatusEnum;
import top.fuyuaaa.shadowpuppets.dao.GoodsCommentDao;
import top.fuyuaaa.shadowpuppets.dao.GoodsOrderDao;
import top.fuyuaaa.shadowpuppets.common.exceptions.CommentException;
import top.fuyuaaa.shadowpuppets.common.exceptions.OrderOwnerException;
import top.fuyuaaa.shadowpuppets.common.exceptions.ParamException;
import top.fuyuaaa.shadowpuppets.common.holders.LoginUserHolder;
import top.fuyuaaa.shadowpuppets.mapstruct.CommentConverter;
import top.fuyuaaa.shadowpuppets.model.LoginUserInfo;
import top.fuyuaaa.shadowpuppets.model.PageVO;
import top.fuyuaaa.shadowpuppets.model.po.GoodsCommentPO;
import top.fuyuaaa.shadowpuppets.model.po.GoodsOrderInfoPO;
import top.fuyuaaa.shadowpuppets.model.po.GoodsOrderPO;
import top.fuyuaaa.shadowpuppets.model.qo.CommentQO;
import top.fuyuaaa.shadowpuppets.model.qo.OrderCommentQO;
import top.fuyuaaa.shadowpuppets.model.vo.GoodsCommentVO;
import top.fuyuaaa.shadowpuppets.service.GoodsCommentService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: fuyuaaa
 * @creat: 2019-04-26 22:54
 */
@Service
@SuppressWarnings("all")
public class GoodsCommentServiceImpl implements GoodsCommentService {

    @Autowired
    GoodsCommentDao goodsCommentDao;

    @Autowired
    GoodsOrderDao goodsOrderDao;

    @Override
    @NeedLogin
    @Transactional(rollbackFor = Exception.class)
    public void addComment(OrderCommentQO orderCommentQO) {
        validateCommentParam(orderCommentQO);
        validateOrderOwner(orderCommentQO);

        //考虑到一个订单可能有好多个商品 + 评论是针对订单级别，SO 这样搞
        List<GoodsOrderInfoPO> goodsOrderInfoPOList = goodsOrderDao.getOrderInfoByOrderId(orderCommentQO.getOrderId());
        List<Integer> goodsIdList = goodsOrderInfoPOList.stream()
                .map(goodsOrderInfoPO -> goodsOrderInfoPO.getGoodsId())
                .collect(Collectors.toList());
        goodsIdList.forEach(goodsId -> {
            this.checkHaveCommented(orderCommentQO.getOrderId());
            GoodsCommentPO goodsCommentPO = new GoodsCommentPO();
            goodsCommentPO.setOrderId(orderCommentQO.getOrderId());
            goodsCommentPO.setUserId(LoginUserHolder.instance().get().getId());
            goodsCommentPO.setStarLevel(orderCommentQO.getStarLevel());
            goodsCommentPO.setGoodsId(goodsId);
            goodsCommentPO.setContent(orderCommentQO.getContent());
            //循环Insert，虽然很弱智，但我还是写了，嘤嘤嘤
            if (goodsCommentDao.insert(goodsCommentPO) != 1) {
                throw new CommentException(ExEnum.COMMENT_ADD_ERROR.getMsg());
            }
        });
        goodsOrderDao.updateOrderStatus(OrderStatusEnum.FINISHED.code(), orderCommentQO.getOrderId());
    }

    @Override
    public void removeComment(Integer commentId) {
        if (goodsCommentDao.delete(commentId) != 1) {
            throw new CommentException(ExEnum.COMMENT_REMOVE_ERROR.getMsg());
        }
    }

    @Override
    public PageVO<GoodsCommentVO> getListByGoodsId(CommentQO commentQO) {
        fillCommentQO(commentQO);
        PageHelper.startPage(commentQO.getPageNum(), commentQO.getPageSize());
        List<GoodsCommentPO> listByGoodsId = goodsCommentDao.findListByGoodsId(commentQO.getGoodsId());
        PageInfo<GoodsCommentPO> pageInfo = new PageInfo<>(listByGoodsId);
        PageVO<GoodsCommentVO> pageVO = new PageVO<>(pageInfo);
        List<GoodsCommentVO> commentVOList = CommentConverter.INSTANCE.toGoodsCommentVOList(listByGoodsId);
        pageVO.setList(commentVOList);
        return pageVO;
    }

    //==============================  private help methods  ==============================


    /**
     * 校验评论参数
     *
     * @param orderCommentQO 评论参数
     */
    private void validateCommentParam(OrderCommentQO orderCommentQO) {
        if (StringUtils.isEmpty(orderCommentQO.getContent())) {
            throw new ParamException("请输入评价内容");
        }
        if (null == orderCommentQO.getStarLevel() || 0 == orderCommentQO.getStarLevel()) {
            throw new ParamException("请选择满意程度");
        }
    }

    /**
     * 填充评论参数
     *
     * @param commentQO 评论参数
     */
    private void fillCommentQO(CommentQO commentQO) {
        if (null == commentQO) {
            commentQO = new CommentQO();
        }
        if (null == commentQO.getPageNum() || 0 >= commentQO.getPageNum()) {
            commentQO.setPageNum(1);
        }
        if (null == commentQO.getPageSize() || 0 >= commentQO.getPageSize()) {
            commentQO.setPageSize(10);
        }
    }

    /**
     * 检验是否已经评论过
     *
     * @param goodsId 商品id
     */
    private void checkHaveCommented(String orderId) {
        if (goodsCommentDao.findByOrderId(orderId) > 0) {
            throw new CommentException(ExEnum.COMMENT_HAVE_EXIST.getMsg());
        }
    }

    /**
     * 校验订单是否属于用户，是否已经评论
     *
     * @param orderId 订单ID
     */
    private void validateOrderOwner(OrderCommentQO orderCommentQO) {
        GoodsOrderPO goodsOrderPO = goodsOrderDao.getById(orderCommentQO.getOrderId());
        LoginUserInfo userInfo = LoginUserHolder.instance().get();
        if (!userInfo.getId().equals(goodsOrderPO.getUserId())) {
            throw new OrderOwnerException(ExEnum.IS_NOT_ORDER_OWNER.getMsg());
        }
    }
}
