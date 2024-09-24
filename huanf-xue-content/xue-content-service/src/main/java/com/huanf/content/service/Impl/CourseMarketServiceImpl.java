package com.huanf.content.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huanf.content.domain.entity.CourseMarket;
import com.huanf.content.mapper.CourseMarketMapper;
import com.huanf.content.service.CourseMarketService;
import org.springframework.stereotype.Service;

@Service
public class CourseMarketServiceImpl extends ServiceImpl<CourseMarketMapper,CourseMarket> implements CourseMarketService {

    @Override
    public CourseMarket getCourseMarket(Long courseId) {
        return getById(courseId);
    }
}
