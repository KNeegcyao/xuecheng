package com.huanf.content.service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huanf.base.exception.XueChengPlusException;
import com.huanf.base.utils.JsonUtil;
import com.huanf.content.domain.dto.CourseBaseInfoDto;
import com.huanf.content.domain.dto.CoursePreviewDto;
import com.huanf.content.domain.dto.TeachplanDto;
import com.huanf.content.domain.entity.CourseMarket;
import com.huanf.content.domain.entity.CoursePublish;
import com.huanf.content.domain.entity.CoursePublishPre;
import com.huanf.content.mapper.CourseBaseMapper;
import com.huanf.content.mapper.CoursePublishPreMapper;
import com.huanf.content.service.CourseBaseService;
import com.huanf.content.service.CourseMarketService;
import com.huanf.content.service.CoursePublishService;
import com.huanf.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.CoyoteAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
}
