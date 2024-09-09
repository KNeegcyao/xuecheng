package com.huanf.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanf.content.domain.dto.CourseCategoryDto;
import com.huanf.content.domain.entity.CourseCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
    //使用递归查询分类
    List<CourseCategoryDto> selectTreeNodes(String id);
}
