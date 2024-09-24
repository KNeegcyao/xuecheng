package com.huanf.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huanf.content.domain.entity.CourseMarket;

public interface CourseMarketService extends IService<CourseMarket> {
    CourseMarket getCourseMarket(Long courseId);
}
