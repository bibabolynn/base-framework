package com.yuan.service;

import com.yuan.model.OrderReceipt;
import com.yuan.model.ServiceResponse;

import java.util.List;

/**
 * Created by lynn on 2018/5/15.
 */
public interface ReceiptService {

    ServiceResponse<List<OrderReceipt>> selectReceipts(OrderReceipt receipt, int pageNo, int pageSize);

    ServiceResponse<OrderReceipt> queryById(Long receiptId);

    boolean insertOrderReceipt(OrderReceipt receipt);
}
