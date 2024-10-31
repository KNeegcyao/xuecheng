package com.huanf.learning.service.Impl;

import com.huanf.base.exception.XueChengPlusException;
import com.huanf.base.utils.JsonUtil;
import com.huanf.learning.config.PayNotifyConfig;
import com.huanf.learning.service.MyCourseTableService;
import com.huanf.messagesdk.model.po.MqMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 接收消息通知处理类
 */
@Slf4j
@Service
public class ReceivePayNotifyService {
    @Autowired
    MyCourseTableService myCourseTableService;
    @RabbitListener(queues = PayNotifyConfig.PAYNOTIFY_QUEUE)
    public void receive(Message message, Channel channel) throws InterruptedException {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //解析成消息
        byte[] body = message.getBody();
        String jsonString = new String(body);
        //转成对象
        MqMessage mqMessage = JsonUtil.jsonToObject(jsonString, MqMessage.class);
        log.debug("学习中心服务接收支付结果:{}", mqMessage);
        //解析消息的内容
        //选课id
        String chooseCourseId = null;
        if (mqMessage != null) {
            chooseCourseId = mqMessage.getBusinessKey1();
        }
        //订单类型
        String orderType = null;
        if (mqMessage != null) {
            orderType = mqMessage.getBusinessKey2();
        }
        //学习中心服务只要购买课程类的支付订单的结果
        if(orderType.equals("60201")){
            //根据消息内容，更新选课记录表，向我的课程表插入消息
            boolean b = myCourseTableService.saveChooseCourseSuccess(chooseCourseId);
            if(!b){
                XueChengPlusException.cast("保存选课记录状态失败");
            }
        }


    }
}
