package com.huanf.content.service.Impl;

import com.huanf.content.domain.dto.CourseBaseInfoDto;
import com.huanf.content.domain.dto.CoursePreviewDto;
import com.huanf.content.domain.dto.TeachplanDto;
import com.huanf.content.service.CourseBaseService;
import com.huanf.content.service.CoursePublishService;
import com.huanf.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 课程发布相关接口实现
 */
@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {
    @Resource
    CourseBaseService courseBaseService;
    @Resource
    TeachplanService teachplanService;
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
}
