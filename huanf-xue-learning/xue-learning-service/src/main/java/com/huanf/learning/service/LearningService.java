package com.huanf.learning.service;

import com.huanf.base.model.RestResponse;
/**
 * 学习过程管理接口
 */
public interface LearningService {
    /**
     * @description 获取教学视频
     * @param courseId 课程id
     * @param teachplanId 课程计划id
     * @param mediaId 视频文件id
     */
    RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId);
}
