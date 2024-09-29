package com.huanf.content.service.jobhandler;

import com.huanf.base.exception.XueChengPlusException;
import com.huanf.content.domain.dto.CoursePreviewDto;
import com.huanf.content.domain.entity.CoursePublish;
import com.huanf.content.feignclient.CourseIndex;
import com.huanf.content.feignclient.SearchServiceClient;
import com.huanf.content.mapper.CoursePublishMapper;
import com.huanf.content.service.CoursePublishService;
import com.huanf.messagesdk.model.po.MqMessage;
import com.huanf.messagesdk.service.MessageProcessAbstract;
import com.huanf.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * 课程发布的任务类
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    @Resource
    CoursePublishService coursePublishService;
    //任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);
        //参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
        process(shardIndex,shardTotal,"course_publish",30,60);
    }

    //执行课程发布的逻辑方法
    //如果此方法抛出了异常，说明任务执行失败
    @Override
    public boolean execute(MqMessage mqMessage) {
        //从mqMessage中拿到课程id
        Long courseId = Long.valueOf(mqMessage.getBusinessKey1());
        //课程静态化上传到minio
        generateCourseHtml(mqMessage,courseId);
        //向ElasticSearch写索引
        saveCourseIndex(mqMessage,courseId);
        //向redis写缓存


        return true;
    }

    //生成课程静态化页面并上传至文件系统
    private void generateCourseHtml(MqMessage mqMessage,long courseId){
        //1.做任务幂等性处理
        //消息id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //查询数据库取出该阶段执行状态
        int stageOne = mqMessageService.getStageOne(taskId);
        if (stageOne == 1) {
            log.debug("当前阶段为静态化课程信息任务，已完成，无需再次处理，任务信息：{}", mqMessage);
            return;
        }
        //2.开始进行课程静态化
        File file = coursePublishService.generateCourseHtml(courseId);
        if(file==null){
            XueChengPlusException.cast("生成的静态页面为空");
        }
        //将html上传到minio
        coursePublishService.uploadCourseHtml(courseId,file);
        //有错误都会往外抛，就不会运行到最后一步
        //任务处理完成写任务状态为完成
        mqMessageService.completedStageOne(taskId);
    }

    //保存课程索引信息
    public void saveCourseIndex(MqMessage mqMessage, Long courseId) {
        log.debug("正在保存课程信息索引，课程id:{}", courseId);
        // 1. 获取消息id
        Long id = mqMessage.getId();
        // 2. 获取小任务阶段状态
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageTwo = mqMessageService.getStageTwo(id);
        // 3. 当前小任务完成，无需再次处理
        if (stageTwo == 1) {
            log.debug("当前阶段为创建课程索引任务，已完成，无需再次处理，任务信息：{}", mqMessage);
            return;
        }
        // 4. 远程调用保存课程索引接口，将课程信息上传至ElasticSearch
        Boolean result = coursePublishService.saveCourseIndex(courseId);
        if (result) {
            mqMessageService.completedStageTwo(id);
        }
    }
}
