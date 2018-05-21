package com.yuan.service;

import com.yuan.model.ServiceResponse;
import com.yuan.model.SingleOrder;

import java.util.List;

/**
 * Created by lynn on 2018/5/13.
 */
public interface OrderService {


    boolean addOrder(SingleOrder order);

    boolean updateOrder(SingleOrder order);

    boolean delateOrder(Long orderId);

    SingleOrder getOrderById(Long orderId);

    ServiceResponse<List<SingleOrder>> getOrderList(List<Long> orderIds);
}
