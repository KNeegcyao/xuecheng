package com.huanf.content.service;

import com.huanf.content.domain.dto.CoursePreviewDto;

/**
 * 课程发布接口
 */
public interface CoursePublishService {
    /**
     * @description 获取课程预览信息
     * @param courseId 课程id
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);
}
