package com.yuan.dao;

import com.yuan.model.SingleOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SingleOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(SingleOrder record);

    int insertSelective(SingleOrder record);

    SingleOrder selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(SingleOrder record);

    int updateByPrimaryKey(SingleOrder record);

    List<SingleOrder> selectOrderListsByIds(List<Long> orderIds);
}