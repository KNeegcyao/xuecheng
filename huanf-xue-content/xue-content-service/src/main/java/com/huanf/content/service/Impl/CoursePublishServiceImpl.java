package com.huanf.content.service.Impl;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huanf.base.exception.XueChengPlusException;
import com.huanf.base.utils.JsonUtil;
import com.huanf.content.config.MultipartSupportConfig;
import com.huanf.content.domain.dto.CourseBaseInfoDto;
import com.huanf.content.domain.dto.CoursePreviewDto;
import com.huanf.content.domain.dto.TeachplanDto;
import com.huanf.content.domain.entity.*;
import com.huanf.content.po.CourseIndex;
import com.huanf.content.feignclient.MediaServiceClient;
import com.huanf.content.feignclient.SearchServiceClient;
import com.huanf.content.mapper.CourseBaseMapper;
import com.huanf.content.mapper.CoursePublishMapper;
import com.huanf.content.mapper.CoursePublishPreMapper;
import com.huanf.content.service.CourseBaseService;
import com.huanf.content.service.CourseMarketService;
import com.huanf.content.service.CoursePublishService;
import com.huanf.content.service.TeachplanService;
import com.huanf.messagesdk.model.po.MqMessage;
import com.huanf.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 课程发布相关接口实现
 */
@Slf4j
@Service
public class CoursePublishServiceImpl extends ServiceImpl<CoursePublishPreMapper,CoursePublishPre> implements CoursePublishService{
    @Resource
    CourseBaseService courseBaseService;
    @Resource
    CourseMarketService courseMarketService;
    @Resource
    TeachplanService teachplanService;
    @Resource
    CoursePublishPreMapper coursePublishPreMapper;
    @Resource
    CourseBaseMapper courseBaseMapper;
    @Resource
    MqMessageService mqMessageService;
    @Resource
    MediaServiceClient mediaServiceClient;
    @Resource
    CoursePublishMapper coursePublishMapper;
    @Resource
    SearchServiceClient searchServiceClient;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    RedissonClient redissonClient;

    /**
     * 获取课程预览信息
     * @param courseId 课程id
     * @return
     */
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        //课程基本信息
        CourseBaseInfoDto courseBaseInfo = courseBaseService.getCourseBaseInfo(courseId);
        //课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        return coursePreviewDto;
    }

    /**
     * 提交审核
     * @param companyId
     * @param courseId
     */
    @Transactional
    @Override
    public void commitAudit(Long companyId, Long courseId) {
        CourseBaseInfoDto courseBaseInfo = courseBaseService.getCourseBaseInfo(courseId);
        CourseMarket courseMarket=courseMarketService.getCourseMarket(courseId);
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if(!Objects.equals(courseBaseInfo.getCompanyId(), companyId)){
            XueChengPlusException.cast("只能对该机构课程进行操作");
        }
        //======约束========
        //课程的审核状态为已提交则不允许提交
        String auditStatus = courseBaseInfo.getAuditStatus();
        if(auditStatus.equals("202003")){
            XueChengPlusException.cast("该课程已提交,不允许再被提交");
        }
        //没有上传图片或者没有添加课程计划不允许提交
        if(courseBaseInfo.getPic()==null|| teachplanTree.isEmpty()){
            XueChengPlusException.cast("没有上传图片或者没有添加课程计划不允许提交");
        }
        //查询课程基本信息，课程营销信息，课程计划信息，整合到课程预发布信息
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        BeanUtils.copyProperties(courseBaseInfo,coursePublishPre);
        String courseMarketJson = JSON.toJSONString(courseMarket);
        String teachplan = JsonUtil.listTojson(teachplanTree);
        coursePublishPre.setMarket(courseMarketJson);
        coursePublishPre.setTeachplan(teachplan);
        //设置审核状态
        coursePublishPre.setStatus("202003");
        //教学机构
        coursePublishPre.setCompanyId(companyId);
        //提交时间
        coursePublishPre.setCreateDate(LocalDateTime.now());
        //向课程预发布表course_publish_pre插入数据，如果存在则更新审核状态
        CoursePublishPre coursePublishPre1 = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre1==null){
            //为空，则添加进去
            coursePublishPreMapper.insert(coursePublishPre);
        }else{
            //已存在，更新
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        //更新基本信息表的审核状态为已提交
        //更新课程基本表的审核状态
        courseBaseInfo.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBaseInfo);
    }

    /**
     * 课程发布接口
     * @param companyId
     * @param courseId
     */
    @Transactional
    @Override
    public void publish(Long companyId, Long courseId) {
        //从预发布表获取信息
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        //=====约束======
        if(coursePublishPre==null){
            XueChengPlusException.cast("请先提交课程审核，审核通过才能发布");
        }
        // 课程审核通过方可发布。
        String status = coursePublishPre.getStatus();
        if(!status.equals("202004")){
            XueChengPlusException.cast("课程审核通过方可发布");
        }
        // 本机构只允许发布本机构的课程。
        if(!Objects.equals(companyId, coursePublishPre.getCompanyId())){
            XueChengPlusException.cast("本机构只允许发布本机构的课程");
        }
        //向课程发布表course_publish插入一条记录,记录来源于课程预发布表，如果存在则更新，发布状态为：已发布。
        saveCoursePublish(courseId);
        //向消息表写数据
        saveCoursePublishMessage(courseId);
        //删除课程预发布表的对应记录。
        coursePublishPreMapper.deleteById(courseId);

    }

    /**
     * 课程静态化
     * @param courseId  课程id
     * @return
     */
    @Override
    public File generateCourseHtml(Long courseId) {

        //静态化文件
        File htmlFile  = null;

        try {
            //配置freemarker
            Configuration configuration = new Configuration(Configuration.getVersion());

            //加载模板
            //选指定模板路径,classpath下templates下
            //得到classpath路径
            String classpath = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //设置字符编码
            configuration.setDefaultEncoding("utf-8");

            //指定模板文件名称
            Template template = configuration.getTemplate("course_template.ftl");

            //准备数据
            CoursePreviewDto coursePreviewInfo = this.getCoursePreviewInfo(courseId);

            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);

            //静态化
            //参数1：模板，参数2：数据模型
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
//            System.out.println(content);
            //将静态化内容输出到文件中
            InputStream inputStream = IOUtils.toInputStream(content);
            //创建静态化文件
            htmlFile = File.createTempFile("course",".html");
            log.debug("课程静态化，生成静态文件:{}",htmlFile.getAbsolutePath());
            //输出流
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("课程静态化异常:{}",e.toString());
            XueChengPlusException.cast("课程静态化异常");
        }

        return htmlFile;
    }


    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        try {
            MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
            String course = mediaServiceClient.uploadFile(multipartFile, "course/"+courseId+".html");
            if(course==null){
                log.debug("远程调用走降级逻辑得到的结果为null,课程id:{}",courseId);
                XueChengPlusException.cast("上传静态文件异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            XueChengPlusException.cast("上传静态文件异常");
        }
    }

    @Override
    public Boolean saveCourseIndex(Long courseId) {
        // 1. 取出课程发布信息
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        // 2. 拷贝至课程索引对象
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish, courseIndex);
        // 3. 远程调用搜索服务API，添加课程索引信息
        Boolean result = searchServiceClient.add(courseIndex);
        if (!result) {
            XueChengPlusException.cast("添加索引失败");
        }
        return true;
//        // 1. 取出课程发布信息
//        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
//        if (coursePublish == null) {
//            log.error("课程发布信息未找到，课程ID: {}", courseId);
//            throw new XueChengPlusException("课程发布信息未找到");
//        }
//
//        // 2. 拷贝至课程索引对象
//        CourseIndex courseIndex = new CourseIndex();
//        try {
//            BeanUtils.copyProperties(coursePublish, courseIndex);
//        } catch (Exception e) {
//            log.error("拷贝课程发布信息至课程索引对象失败，课程ID: {}", courseId, e);
//            throw new XueChengPlusException("课程索引对象生成失败");
//        }
//
//        // 3. 远程调用搜索服务API，添加课程索引信息
//        Boolean result = searchServiceClient.add(courseIndex);
//        if (Boolean.FALSE.equals(result)) {
//            log.error("远程调用添加课程索引失败，课程ID: {}", courseId);
//            throw new XueChengPlusException("添加索引失败");
//        }
//        log.info("成功添加课程索引，课程ID: {}", courseId);
//        return true;
    }


    /**
     * 查询课程发布信息
     * @param courseId
     * @return
     */
    @Override
    public CoursePublish getCoursePublish(Long courseId) {
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        return coursePublish;
    }



    /**
     * 保存课程发布信息
     *
     * @param courseId 课程id
     */
    private void saveCoursePublish(Long courseId) {
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            XueChengPlusException.cast("课程预发布数据为空");
        }
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        // 设置发布状态为已发布
        coursePublish.setStatus("203002");
        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
        // 有则更新，无则新增
        if (coursePublishUpdate == null) {
            coursePublishMapper.insert(coursePublish);
        } else {
            coursePublishMapper.updateById(coursePublish);
        }
        // 更新课程基本信息表的发布状态为已发布
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setAuditStatus("203002");
        courseBaseMapper.updateById(courseBase);
    }

    /**
     * 保存消息表
     *
     * @param courseId 课程id
     */
    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            XueChengPlusException.cast("添加消息记录失败");
        }
    }
    /**
     * @description 查询缓存中的课程信息
     * @param courseId
     * @return com.xuecheng.content.model.po.CoursePublish
     */
    @Override
    public CoursePublish getCoursePublishCache(Long courseId) {
        //Cache查询缓存
        Object result = redisTemplate.opsForValue().get("course:" + courseId);
        if (result != null) {
            //System.out.println("==========查询缓存=========");
            //缓存中有数据
            String jsonString = result.toString();
            if (jsonString.equals("null")) {
                return null;
            }
            CoursePublish coursePublish = JsonUtil.jsonToObject(jsonString, CoursePublish.class);
            return coursePublish;
        } else {
            RLock lock = redissonClient.getLock("coursequerylock:" + courseId);
            //获取分布式锁
            lock.lock();
            try {
                result = redisTemplate.opsForValue().get("course:" + courseId);
                if (result != null) {
                    //System.out.println("==========查询缓存=========");
                    //缓存中有数据
                    String jsonString = result.toString();
                    if (jsonString.equals("null")) {
                        return null;
                    }
                    CoursePublish coursePublish = JsonUtil.jsonToObject(jsonString, CoursePublish.class);
                    return coursePublish;
                }
                System.out.println("==========查询数据库==========");
                //缓存中没有数据
                //先从数据库中查找
                CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
                //保存到缓存中
                redisTemplate.opsForValue().set("course:" + courseId, JsonUtil.objectTojson(coursePublish), 30, TimeUnit.SECONDS);
                return coursePublish;
            } finally {
                //手动释放锁
                lock.isLocked();
            }
        }
    }
}