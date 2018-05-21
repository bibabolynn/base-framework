package com.yuan.service.impl;

import com.yuan.dao.OrderReceiptMapper;
import com.yuan.model.OrderReceipt;
import com.yuan.model.ServiceResponse;
import com.yuan.redis.RedisTemplateCache;
import com.yuan.service.ReceiptService;
import com.yuan.util.constant.BaseResponseCode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lynn on 2018/5/15.
 */
@Service
public class ReceiptServiceImpl implements ReceiptService {
    private static final Logger logger = LoggerFactory.getLogger(ReceiptServiceImpl.class);
    @Resource
    OrderReceiptMapper orderReceiptMapper;

    @Resource
    RedisTemplateCache redisTemplateCache;

    @Override
    public ServiceResponse<List<OrderReceipt>> selectReceipts(OrderReceipt receipt, int pageNo, int pageSize){
        ServiceResponse<List<OrderReceipt>> response = new ServiceResponse<>();
        if(pageSize > 100){
            response.setCode(BaseResponseCode.BUSINESS_ILLEGAL_OP.getCode());
            response.setMsg("每页不能超过100条记录");
            return response;
        }
        List<OrderReceipt> result;
        try{
            result = orderReceiptMapper.getReceiptsByPage(receipt,pageNo,pageSize);
        }catch (Exception e){
            logger.error("分页查询回执单异常",e);
            response.setCode(BaseResponseCode.FAILURE.getCode());
            response.setMsg("分页查询回执单异常");
            return response;
        }
        response.setResult(result);
        return response;
    }

    @Override
    public ServiceResponse<OrderReceipt> queryById(Long receiptId) {
        ServiceResponse<OrderReceipt> response = new ServiceResponse<>();
        if(receiptId == null){
            response.setCode(BaseResponseCode.PARAM_ERROR.getCode());
            response.setMsg("回执单号为空");
            return response;
        }
        OrderReceipt result;
        try{
            result = orderReceiptMapper.selectByPrimaryKey(receiptId);
        }catch (Exception e){
            logger.error("根据回执单号查询回执单异常",e);
            response.setCode(BaseResponseCode.FAILURE.getCode());
            response.setMsg("根据回执单号查询回执单异常");
            return response;
        }
        response.setResult(result);
        return response;
    }

    //TODO 新增回执单，要生成序号，
    @Resource
    public boolean insertOrderReceipt(OrderReceipt receipt){
        try{
            //TODO 计算总金额

            return orderReceiptMapper.insert(receipt) > 1;
        }catch (Exception e){
            logger.error("插入回执单异常");
        }
        return false;

    }

    /** 生成回执单序号 **/
    private long getReceiptNo() throws Exception {
        try {
            //TODO 要注意分布式锁 根据一个固定的值，因为所有序号不允许重复
//            RedisDistributionLockUtil.lock(CACHE_HEADER_STATION_ORDER_NO_LOCK + dbOrderMain.getDeliveryStationNo());

            DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String key = simpleDateFormat.format(new Date());
            logger.info("getMaxOrderNum Redis的key: " + key);

            String value = redisTemplateCache.get(key);
            if (StringUtils.isNotBlank(value)) {
                logger.info("getReceiptNo Redis取值：" + value);
                long newValue = Long.parseLong(value.trim()) + 1;
                String new_value = String.valueOf(newValue);
                logger.info("getReceiptNo Redis设置新值：" + new_value);
                redisTemplateCache.set(key, new_value);
                return newValue;
            } else {
                redisTemplateCache.set(key, "1");
                return 1;
            }
        } catch (Exception e) {
            logger.error("生成回执单序号异常" , e);
            return 0;
        }finally {
//释放锁
//            RedisDistributionLockUtil.unLock(CACHE_HEADER_STATION_ORDER_NO_LOCK + dbOrderMain.getDeliveryStationNo());

        }
    }

}
