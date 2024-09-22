package com.huanf.media.service.jobhandler;

import com.huanf.domain.entity.MediaProcess;
import com.huanf.media.service.MediaFilesService;
import com.huanf.media.service.MediaProcessService;
import com.huanf.media.service.impl.MediaProcessServiceImpl;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.huanf.base.utils.*;
import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 任务处理类
 */
@Component
@Slf4j
public class VideoTask {
    @Resource
    MediaProcessService mediaFileProcessService;
    @Resource
    MediaFilesService mediaFilesService;
    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegpath;
    /**
     * 视频处理任务
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws InterruptedException {
        //分片参数
        int shardIndex = XxlJobHelper.getShardIndex();//执行器的序号，从0开始
        int shardTotal = XxlJobHelper.getShardTotal();//执行器总数

        //确定cpu核心数
        int processors = Runtime.getRuntime().availableProcessors();
        //查询待处理任务
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, processors);

        //任务数量
        int size = mediaProcessList.size();
        log.debug("取到的视频处理任务数："+size);
        if (size == 0) {
            return;
        }
        //创建一个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        //使用计数器
        CountDownLatch countDownLatch = new CountDownLatch(size);
        mediaProcessList.forEach(mediaProcess -> {
            if (mediaProcess == null) {
                log.error("mediaProcess 对象为 null");
                return;
            }

            //将任务加入线程池
            executorService.execute(()->{
                try {
                    //任务执行逻辑
                    Long taskId = mediaProcess.getId();
                    if (taskId == null) {
                        log.error("任务ID为空");
                        return;
                    }

                    //文件id就是md5值
                    String fileId = mediaProcess.getFileId();
                    //开启任务
                    boolean b = mediaFileProcessService.startTask(taskId);
                    if(!b){
                        log.error("抢占任务失败：{}",taskId);
                        return;
                    }
                    //下载minio视频到本地
                    String bucket = mediaProcess.getBucket();
                    String filePath = mediaProcess.getFilePath();
                    File file = mediaFilesService.downloadFileFromMinIO(bucket,filePath);
                    if(file==null){
                        log.debug("下载视频出错,任务id:{},objectName:{}",taskId,filePath);
                        //保存任务失败的结果
                        mediaFileProcessService.saveProcessFinishStatus(taskId,"3",fileId,null,"下载视频到本地失败");
                        return;
                    }
                    //源avi视频的路径
                    String video_path = file.getAbsolutePath();
                    //转换后mp4文件的名称
                    String mp4_name = fileId + ".mp4";
                    //转换后mp4文件的路径
                    //先创建一个临时文件，作为转换后的文件
                    File mp4File=null;
                    try {
                        mp4File = File.createTempFile("minio", ".mp4");
                    } catch (IOException e) {
                        log.debug("创建临时文件异常：{}",e.getMessage());
                        mediaFileProcessService.saveProcessFinishStatus(taskId,"3",fileId,null,"创建临时文件异常");
                        return;
                    }
                    String mp4_path=mp4File.getAbsolutePath();
                    //创建工具类对象
                    Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpegpath, video_path, mp4_name, mp4_path);
                    //开始转换视频，成功将返回success
                    String result = mp4VideoUtil.generateMp4();
                    if(!result.equals("success")){
                        log.error("视频转码失败，原因:{},bucket:{},object:{}",result,bucket,filePath);
                        mediaFileProcessService.saveProcessFinishStatus(taskId,"3",fileId,null,result);
                        return ;
                    }
                    //上传到minio
                    boolean bw = mediaFilesService.addMediaFilesToMinIO(filePath, "video/mp4", bucket, filePath);
                    if(!bw){
                        log.debug("上传mp4到minio失败,taskId:{}",taskId );
                        mediaFileProcessService.saveProcessFinishStatus(taskId,"3",fileId,null,"上传MP4到minio失败");
                        return;
                    }
                    //mp4文件的url
                    String url="/" + bucket + "/" + mediaFilesService.getFilePathByMd5(fileId,".mp4");
                    //只有任务状态为成功
                    mediaFileProcessService.saveProcessFinishStatus(taskId,"2",fileId,url,result);
                } finally {
                    //计数器减去1
                    countDownLatch.countDown();
                }

            });
        });
        //阻塞，指定最大限度的等待时间，阻塞最多等待一定的时间后就接触阻塞
        countDownLatch.await(30, TimeUnit.MINUTES);

    }
}
