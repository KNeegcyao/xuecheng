package com.huanf.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huanf.content.domain.dto.CoursePreviewDto;
import com.huanf.content.domain.entity.CoursePublish;
import com.huanf.content.domain.entity.CoursePublishPre;

import java.io.File;

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
    /**
     * @description 课程静态化
     * @param courseId  课程id
     */
    File generateCourseHtml(Long courseId);
    /**
     * @description 上传课程静态化页面
     * @param file  静态化文件
     */
    void  uploadCourseHtml(Long courseId, File file);

    /**
     * 保存课程索引
     * @param courseId  课程id
     * @return
     */
    Boolean saveCourseIndex(Long courseId);

}
