package com.huanf.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.huanf.domain.entity.CourseMarket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMarketMapper extends BaseMapper<CourseMarket> {
}
