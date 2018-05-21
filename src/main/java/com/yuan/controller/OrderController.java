package com.yuan.controller;

import com.yuan.model.SingleOrder;
import com.yuan.service.OrderService;
import com.yuan.util.LogUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * Created by lynn on 2018/5/12.
 */
@RestController
@RequestMapping(value = "/order")
public class OrderController {

    @Resource
    OrderService orderService;
    /**
     * 添加新货单
     */
    @RequestMapping(value = "/addOrder")
    public ModelAndView add(SingleOrder order,String receiptNo){
        ModelAndView model = new ModelAndView("");
        if(order == null){
            LogUtil.error("添加货单异常，货单信息为空");
        }
        //生成序号，计算金额和门扇尺寸
        try{
            orderService.addOrder(order);
        }catch (Exception e){
            LogUtil.error("添加货单异常，货单信息："+ (order));
        }
        return model;
    }


}
