package com.huanf.content.service.jobhandler;

import com.huanf.messagesdk.model.po.MqMessage;
import com.huanf.messagesdk.service.MessageProcessAbstract;
import com.huanf.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 课程发布的任务类
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

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
        //消息id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //做任务幂等性处理
        //查询数据库取出该阶段执行状态
        int stageOne = mqMessageService.getStageOne(taskId);
        if(stageOne>0){
            log.error("课程静态化任务完成，无需处理");
            return ;
        }
        //开始进行课程静态化

        //任务处理完成写任务状态为完成
        mqMessageService.completedStageOne(taskId);
    }

    //保存课程索引信息
    public void saveCourseIndex(MqMessage mqMessage,long courseId){
        //消息id
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        //取出第二个阶段状态
        int stageTwo = mqMessageService.getStageTwo(taskId);
        if(stageTwo>0){
            log.error("课程静态化任务完成，无需处理");
            return ;
        }
        //查询课程信息，调用搜索服务添加索引

        //任务处理完成写任务状态为完成
        mqMessageService.completedStageTwo(taskId);
    }
    //将课程信息缓存至redis

}
