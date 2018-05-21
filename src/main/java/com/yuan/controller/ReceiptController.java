package com.yuan.controller;

import com.yuan.model.OrderReceipt;
import com.yuan.model.ServiceResponse;
import com.yuan.service.OrderService;
import com.yuan.service.ReceiptService;
import com.yuan.util.BaseException;
import com.yuan.util.JSONUtil;
import com.yuan.util.LogUtil;
import com.yuan.util.constant.BaseResponseCode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lynn on 2018/5/15.
 */
@RestController
@RequestMapping(value = "/receipt")
public class ReceiptController {
    private static final Logger logger = LoggerFactory.getLogger(ReceiptController.class);
    @Resource
    ReceiptService receiptService;
    @Resource
    OrderService orderService;

    @RequestMapping(value = "list")
    public ModelAndView list(HttpServletRequest request, OrderReceipt receipt) throws  BaseException{
        ModelAndView modelAndView = new ModelAndView("receipt/queryReceipts");
        try{
            if(receipt == null){
                throw new BaseException(BaseResponseCode.PARAM_ERROR.getCode(),"参数错误");
            }
            int pageNo = 0;
            int pageSize = 10;
            if (request.getParameter("pageNo") != null) {
                pageNo = Integer.parseInt(request.getParameter("pageNo"));
                pageSize = Integer.parseInt(request.getParameter("pageSize"));
            }
            ServiceResponse response = receiptService.selectReceipts(receipt,pageNo,pageSize);
            if(response != null && BaseResponseCode.SUCCESS.getCode().equals(response.getCode()) ){
                modelAndView.addObject("receipts",response.getResult());
            }
        }catch (Exception e){
            logger.error("查询回执单列表异常，param:"+ JSONUtil.beanToJson(receipt),e);
        }
        return modelAndView;

    }

    @RequestMapping(value = "queryById")
    public ModelAndView queryById(Long receiptId){
        ModelAndView model = new ModelAndView("/receipt/receiptDetail");
        try{
            ServiceResponse<OrderReceipt> response = receiptService.queryById(receiptId);
            if(response != null && BaseResponseCode.SUCCESS.getCode().equals(response.getCode()) ){
                model.addObject("receipt",response.getResult());
                String orderIdString = response.getResult().getOrderIdList();
                if(StringUtils.isNotBlank(orderIdString)){
                    List<Long> orderIds = new ArrayList<>();
                    model.addObject("orderList",orderService.getOrderList(orderIds));
                }

            }
        }catch (Exception e){
            logger.error("根据回执单号查询回执单信息异常，回执单号："+ (receiptId));
        }
        return model;
    }

    @RequestMapping(value = "add")
    @ResponseBody
    public ServiceResponse add(OrderReceipt receipt){
        ServiceResponse response =  new ServiceResponse();
        try{
            receiptService.insertOrderReceipt(receipt);
        }catch (Exception e){

        }
        return response;
    }
}
