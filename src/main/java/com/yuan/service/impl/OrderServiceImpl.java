package com.yuan.service.impl;

import com.yuan.dao.SingleOrderMapper;
import com.yuan.model.ServiceResponse;
import com.yuan.model.SingleOrder;
import com.yuan.service.OrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lynn on 2018/5/13.
 */
@Service
public class OrderServiceImpl implements OrderService{
    @Resource
    SingleOrderMapper singleOrderMapper;
    @Override
    public boolean addOrder(SingleOrder order) {

        try{
           return singleOrderMapper.insert(order) > 0;
        }catch (Exception e){

        }

        return false;
    }

    @Override
    public boolean updateOrder(SingleOrder order) {
        try{
            return singleOrderMapper.updateByPrimaryKeySelective(order) > 0;
        }catch (Exception e){

        }
        return false;
    }

    @Override
    public boolean delateOrder(Long orderId) {
        try{
            return singleOrderMapper.deleteByPrimaryKey(orderId) > 0;
        }catch (Exception e){

        }
        return false;
    }

    @Override
    public SingleOrder getOrderById(Long orderId) {
        try{
            return singleOrderMapper.selectByPrimaryKey(orderId) ;
        }catch (Exception e){

        }
        return null;
    }

    public ServiceResponse<List<SingleOrder>> getOrderList(List<Long> orderIds){
        ServiceResponse<List<SingleOrder>> response = new ServiceResponse<>();
        List<SingleOrder> result = null;
        try{
            result = singleOrderMapper.selectOrderListsByIds(orderIds);
        }catch (Exception e){

        }
        response.setResult(result);
        return  response;
    }
}
