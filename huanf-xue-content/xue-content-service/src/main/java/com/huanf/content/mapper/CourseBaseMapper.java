package com.huanf.content.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanf.domain.entity.CourseBase;

/**
 * 课程基本信息(CourseBase)表数据库访问层
 *
 * @author makejava
 * @since 2024-03-30 22:36:12
 */
public interface CourseBaseMapper extends BaseMapper<CourseBase> {

    Page<CourseBase> coursePage(Page<CourseBase> page, LambdaQueryWrapper<CourseBase> queryWrapper);
}