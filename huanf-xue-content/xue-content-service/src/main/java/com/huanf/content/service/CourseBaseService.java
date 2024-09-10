package com.huanf.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huanf.base.model.PageParams;
import com.huanf.base.model.PageResult;
import com.huanf.content.domain.dto.AddCourseDto;
import com.huanf.content.domain.dto.CourseBaseInfoDto;
import com.huanf.content.domain.dto.EditCourseDto;
import com.huanf.content.domain.dto.QueryCourseParamsDto;
import com.huanf.content.domain.entity.CourseBase;

/**
 * 课程基本信息(CourseBase)表服务接口
 *
 * @author makejava
 * @since 2024-03-30 22:33:49
 */
public interface CourseBaseService extends IService<CourseBase> {

    /**
     * 分页查询
     * @param pageParams 分页参数
     * @param dto 查询条件
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto dto);

    /**
     * 新增课程
     * @param companyId 机构id
     * @param addCourseDto 课程信息
     * @return 课程详细信息
     */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * 根据课程id查询
     * @param courseId 课程ID
     * @return
     */
    CourseBaseInfoDto getCourseBaseInfo(long courseId);

    CourseBaseInfoDto modifCourseBase(Long companyId, EditCourseDto editCourseDto);
}