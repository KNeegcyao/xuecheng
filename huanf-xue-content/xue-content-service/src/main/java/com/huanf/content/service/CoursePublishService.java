package com.huanf.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huanf.content.domain.dto.CoursePreviewDto;
import com.huanf.content.domain.entity.CoursePublishPre;

/**
 * 课程发布接口
 */
public interface CoursePublishService extends IService<CoursePublishPre> {
    /**
     * @description 获取课程预览信息
     * @param courseId 课程id
     */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * 提交审核
     * @param companyId
     * @param courseId
     */
    void commitAudit(Long companyId, Long courseId);

    /**
     * 课程发布接口
     * @param companyId
     * @param courseId
     */
    void publish(Long companyId, Long courseId);
}
