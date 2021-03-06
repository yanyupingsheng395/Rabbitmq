package com.tuling.service.impl;

import com.tuling.bo.MsgTxtBo;
import com.tuling.entity.MessageContent;
import com.tuling.entity.ProductInfo;
import com.tuling.enumration.MsgStatusEnum;
import com.tuling.exception.BizExp;
import com.tuling.mapper.MsgContentMapper;
import com.tuling.mapper.ProductInfoMapper;
import com.tuling.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 扣减库存。这个类中保持的一个事务。如果扣减失败，那么库存不变
 */
@Service
@Slf4j
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductInfoMapper productInfoMapper;

    @Autowired
    private MsgContentMapper msgContentMapper;

    @Transactional
    @Override
    public boolean updateProductStore(MsgTxtBo msgTxtBo) {
        boolean updateFlag = true;
        try{
            //扣减库存
            productInfoMapper.updateProductStoreById(msgTxtBo.getProductNo());

            //更新消息表状态。将消息状态转成3，消费成功。
            MessageContent messageContent = new MessageContent();
            messageContent.setMsgId(msgTxtBo.getMsgId());
            messageContent.setUpdateTime(new Date());
            messageContent.setMsgStatus(MsgStatusEnum.CONSUMER_SUCCESS.getCode());
            msgContentMapper.updateMsgStatus(messageContent);

            //System.out.println(1/0);
        }catch (Exception e) {
            log.error("更新数据库失败:{}",e);
            throw new BizExp(0,"更新数据库异常");
        }
        return updateFlag;
    }
}
