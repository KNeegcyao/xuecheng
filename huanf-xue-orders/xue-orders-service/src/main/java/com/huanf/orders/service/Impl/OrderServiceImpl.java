package com.huanf.orders.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huanf.base.exception.XueChengPlusException;
import com.huanf.base.utils.IdWorkerUtils;
import com.huanf.base.utils.JsonUtil;
import com.huanf.base.utils.QRCodeUtil;
import com.huanf.messagesdk.model.po.MqMessage;
import com.huanf.messagesdk.service.MqMessageService;
import com.huanf.orders.config.AlipayConfig;
import com.huanf.orders.config.PayNotifyConfig;
import com.huanf.orders.mapper.XcOrdersGoodsMapper;
import com.huanf.orders.mapper.XcOrdersMapper;
import com.huanf.orders.mapper.XcPayRecordMapper;
import com.huanf.orders.model.dto.AddOrderDto;
import com.huanf.orders.model.dto.PayRecordDto;
import com.huanf.orders.model.dto.PayStatusDto;
import com.huanf.orders.model.po.XcOrders;
import com.huanf.orders.model.po.XcOrdersGoods;
import com.huanf.orders.model.po.XcPayRecord;
import com.huanf.orders.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单相关接口
 */
@Slf4j
@Service

public class OrderServiceImpl implements OrderService {
    @Autowired
    XcOrdersMapper ordersMapper;
    @Autowired
    XcOrdersGoodsMapper ordersGoodsMapper;
    @Autowired
    XcPayRecordMapper payRecordMapper;
    @Autowired
    OrderServiceImpl currentProxy;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    MqMessageService mqMessageService;
    @Value("${pay.qrcodeurl}")
    String qrcodeurl;
    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;
    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;
    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) throws IOException {
        //完成插入订单信息，订单表和订单明细表
        //进行幂等性判断，同一个选课记录只能有一个订单
        XcOrders xcOrders = saveXcOrders(userId, addOrderDto);
        // 插入支付记录
        XcPayRecord payRecord = createPayRecord(xcOrders);
        Long payNo = payRecord.getPayNo();
        // 生成支付二维码
        QRCodeUtil qrCodeUtil = new QRCodeUtil();
        String url = String.format(qrcodeurl, payNo);
        String qrCode=null; //二维码图片
        try {
            qrCode = qrCodeUtil.createQRCode(url, 200, 200);
        }catch (IOException e){
            XueChengPlusException.cast("生成二维码失效");
        }
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord,payRecordDto);
        payRecordDto.setQrcode(qrCode);
        return payRecordDto;
    }

    /**
     * 查询支付记录
     * @param payNo  交易记录号
     * @return
     */
    @Override
    public XcPayRecord getPayRecordByPayno(String payNo) {
        XcPayRecord xcPayRecord = payRecordMapper.selectOne(new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo));
        return xcPayRecord;
    }

    /**
     * 查询支付结果
     * @param payNo
     * @return
     */
    @Override
    public PayRecordDto queryResult(String payNo) {
        //调用支付宝接口查询支付结果
        PayStatusDto payStatusDto = queryResultFromAlipay(payNo);
        //拿到支付结果更新支付记录表和订单表的支付状态
        currentProxy.saveAliPayStatus(payStatusDto);
        //要返回最新的支付记录信息
        XcPayRecord payRecord = getPayRecordByPayno(payNo);
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord,payRecordDto);
        return payRecordDto;
    }

    /**
     * 请求支付宝查询支付结果
     * @param payNo
     * @return
     */
    public PayStatusDto queryResultFromAlipay(String payNo){
        //========请求支付宝查询支付结果=============
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, "json", AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE); //获得初始化的AlipayClient
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", payNo);
        request.setBizContent(bizContent.toString());
        String body=null;
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            body=response.getBody();
            if (!response.isSuccess()) {
                XueChengPlusException.cast("请求支付查询查询失败");
            }
        } catch (AlipayApiException e) {
            log.error("请求支付宝查询支付结果异常:{}", e.toString(), e);
            XueChengPlusException.cast("请求支付查询查询失败");
        }
        Map bodyMap = JSON.parseObject(body, Map.class);
        Map alipay_trade_query_response = (Map) bodyMap.get("alipay_trade_query_response");
        //获取支付结果
        String trade_no = (String) alipay_trade_query_response.get("trade_no");
        String trade_status = (String) alipay_trade_query_response.get("trade_status");
        String total_amount = (String) alipay_trade_query_response.get("trade_amount");
        PayStatusDto payStatusDto = new PayStatusDto();
        payStatusDto.setOut_trade_no(payNo);
        payStatusDto.setTrade_no(trade_no);//支付宝的交易号
        payStatusDto.setTrade_status(trade_status);//交易状态
        payStatusDto.setApp_id(APP_ID);
        payStatusDto.setTotal_amount(total_amount);//总金额
        return payStatusDto;

    }

    /**
     * 更新支付记录表和订单表的支付状态
     * @param payStatusDto 从支付宝查询的信息
     */
    @Transactional
    @Override
    public void saveAliPayStatus(PayStatusDto payStatusDto){
        String payNo = payStatusDto.getOut_trade_no();
        XcPayRecord payRecord = getPayRecordByPayno(payNo);
        if(payRecord==null){
            XueChengPlusException.cast("未找到支付记录");
        }
        XcOrders xcOrders = ordersMapper.selectById(payRecord.getOrderId());
        if(xcOrders==null){
            XueChengPlusException.cast("未找到关联订单");
        }
        String status = payRecord.getStatus();
        //订单已支付
        if(status.equals("600002")){
            return;
        }
        //查询支付宝支付状态
        String tradeStatus = payStatusDto.getTrade_status();
        if(tradeStatus.equals("TRADE_SUCCESS")){
            //支付状态成功，更新记录表
            payRecord.setStatus("601002");
            payRecord.setOutPayNo(payNo);
            payRecord.setOutPayChannel("AliPay");
            payRecord.setPaySuccessTime(LocalDateTime.now());
            int update_1 = payRecordMapper.updateById(payRecord);
            if(update_1<=0){
                XueChengPlusException.cast("更新记录表失败");
            }
            //更新订单表
            xcOrders.setStatus("600002");
            int update_2 = ordersMapper.updateById(xcOrders);
            if(update_2<=0){
                XueChengPlusException.cast("更新订单表失败");
            }
            //将消息存入数据库
            MqMessage mqMessage = mqMessageService.addMessage("payresult_notify", xcOrders.getOutBusinessId(), xcOrders.getOrderType(), null);
            //发送消息
            notifyPayResult(mqMessage);

        }

    }

    /**
     * 发送通知结果
     * @param message
     */
    @Override
    public void notifyPayResult(MqMessage message) {
        //消息内容
        String jsonString = JsonUtil.objectTojson(message);
        //创建一个持久化消息
        Message messageObj = MessageBuilder.withBody(jsonString.getBytes(StandardCharsets.UTF_8)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
        //消息id
        Long id = message.getId();
        //全局消息id
        CorrelationData correlationData = new CorrelationData(id.toString());

        //使用correlationData指定回调方法
        correlationData.getFuture().addCallback(result->{
            if(result.isAck()){
                //消息已经成功发送到交换机
                log.debug("发送消息成功：{}",jsonString);
                //将消息从数据库表删掉
                mqMessageService.completed(id);

            }else{
                //消息发送失败
                log.error("通知支付结果消息发送失败, ID:{}, 原因{}",correlationData.getId(), result.getReason());
            }
        },ex->{
            //发生异常
            log.error("消息发送异常, ID:{}, 原因{}",correlationData.getId(),ex.getMessage());
        });
        //发送信息
        rabbitTemplate.convertAndSend(PayNotifyConfig.PAYNOTIFY_EXCHANGE_FANOUT,"",messageObj,correlationData);
    }

    /**
     * 保存支付记录
     * @param orders
     * @return
     */
    public XcPayRecord createPayRecord(XcOrders orders){
        Long orderId = orders.getId();
        XcOrders xcOrders = ordersMapper.selectById(orderId);
        if(xcOrders==null) {
            //如果此订单不存在不能添加支付记录
            XueChengPlusException.cast("订单不存在");
        }
        String status = xcOrders.getStatus();
        if(status.equals("601002")) {
            //如果此订单结果为成功，不再添加支付记录，避免重复支付
            XueChengPlusException.cast("此订单已支付");
        }
        XcPayRecord payRecord = new XcPayRecord();
        long payNo = IdWorkerUtils.getInstance().nextId();
        payRecord.setPayNo(payNo);
        payRecord.setOrderId(orders.getId());//商品订单号
        payRecord.setOrderName(orders.getOrderName());
        payRecord.setTotalPrice(orders.getTotalPrice());
        payRecord.setCurrency("CNY");
        payRecord.setCreateDate(LocalDateTime.now());
        payRecord.setStatus("601001");//未支付
        payRecord.setUserId(orders.getUserId());
        int insert = payRecordMapper.insert(payRecord);
        if(insert<=0){
            XueChengPlusException.cast("插入支付记录表失败");
        }
        return payRecord;


    }

    /**
     * 保存订单信息
     * @param userId
     * @param addOrderDto
     * @return
     */
    public XcOrders saveXcOrders(String userId,AddOrderDto addOrderDto) {
        //完成插入订单信息，订单表和订单明细表
        //进行幂等性判断，同一个选课记录只能有一个订单
        XcOrders order = getOrderByBusinessId(addOrderDto.getOutBusinessId());
        if(order!=null){
            return order;
        }
        //插入订单表
        order = new XcOrders();
        //生成订单号，雪花算法
        long orderId = IdWorkerUtils.getInstance().nextId();
        order.setId(orderId);
        order.setTotalPrice(addOrderDto.getTotalPrice());
        order.setCreateDate(LocalDateTime.now());
        order.setStatus("600001");//未支付
        order.setUserId(userId);
        order.setOrderType(addOrderDto.getOrderType());
        order.setOrderName(addOrderDto.getOrderName());
        order.setOrderDetail(addOrderDto.getOrderDetail());
        order.setOrderDescrip(addOrderDto.getOrderDescrip());
        order.setOutBusinessId(addOrderDto.getOutBusinessId());//选课记录id
        int insert = ordersMapper.insert(order);
        if(insert<=0){
            XueChengPlusException.cast("插入失败");
        }
        //遍历
        List<XcOrdersGoods> ordersGoods = JsonUtil.jsonToList(addOrderDto.getOrderDetail(), XcOrdersGoods.class);
        if (ordersGoods != null) {
            ordersGoods.forEach(goods->{
                XcOrdersGoods xcOrdersGoods = new XcOrdersGoods();
                BeanUtils.copyProperties(goods,xcOrdersGoods);
                xcOrdersGoods.setOrderId(orderId);//订单号
                //插入订单明细表
                int insert1 = ordersGoodsMapper.insert(xcOrdersGoods);
                if(insert1<=0){
                    log.error("插入订单明细表失败：{}",xcOrdersGoods);
                    XueChengPlusException.cast("插入订单明细表失败");
                }
            });
        }
        return order;
    }

    /**
     * 根据业务id查询订单
     * @param businessId 业务id是选课记录表中的主键
     * @return
     */
    public XcOrders getOrderByBusinessId(String businessId){
        return ordersMapper.selectOne(new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getOutBusinessId, businessId));
    }
}
