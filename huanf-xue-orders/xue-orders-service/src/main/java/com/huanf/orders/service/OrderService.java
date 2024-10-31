package com.huanf.orders.service;

import com.huanf.messagesdk.model.po.MqMessage;
import com.huanf.orders.model.dto.AddOrderDto;
import com.huanf.orders.model.dto.PayRecordDto;
import com.huanf.orders.model.dto.PayStatusDto;
import com.huanf.orders.model.po.XcPayRecord;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

public interface OrderService {
    /**
     * @description 创建商品订单
     * @param addOrderDto 订单信息
     * @return PayRecordDto 支付记录(包括二维码)
     * @author Mr.M
     * @date 2022/10/4 11:02
     */
    PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) throws IOException;
    /**
     * @description 查询支付记录
     * @param payNo  交易记录号
     * @date 2022/10/20 23:38
     */
    XcPayRecord getPayRecordByPayno(String payNo);

    /**
     * 查询支付结果
     * @param payNo
     * @return
     */
    PayRecordDto queryResult(String payNo);

    /**
     * 更新支付记录表和订单表的支付状态
     * @param payStatusDto
     */
    void saveAliPayStatus(PayStatusDto payStatusDto);
    /**
     * 发送通知结果
     * @param message
     */
    void notifyPayResult(MqMessage message);
}
