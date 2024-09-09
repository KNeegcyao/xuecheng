package com.huanf.content.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanf.content.domain.entity.CourseBase;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程基本信息(CourseBase)表数据库访问层
 *
 * @author makejava
 * @since 2024-03-30 22:36:12
 */
@Mapper
public interface CourseBaseMapper extends BaseMapper<CourseBase> {

    Page<CourseBase> coursePage(Page<CourseBase> page, LambdaQueryWrapper<CourseBase> queryWrapper);
}