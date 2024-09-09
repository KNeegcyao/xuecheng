package com.huanf.content.api;

import com.huanf.base.exception.ValidationGroups;
import com.huanf.base.model.PageParams;
import com.huanf.base.model.PageResult;
import com.huanf.content.domain.dto.AddCourseDto;
import com.huanf.content.domain.dto.CourseBaseInfoDto;
import com.huanf.content.domain.dto.EditCourseDto;
import com.huanf.content.domain.dto.QueryCourseParamsDto;
import com.huanf.content.domain.entity.CourseBase;
import com.huanf.content.service.CourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: 35238
 * 功能: 课程信息编辑接口
 * 时间: 2024-03-28 21:10
 */
@Api(value = "课程信息编辑",tags = "课程信息编辑")
@RestController
public class CourseBaseInfoController {
    @Autowired
    CourseBaseService courseBaseService;

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParams){
       return courseBaseService.queryCourseBaseList(pageParams,queryCourseParams);
    }

    @ApiOperation("新增课程接口")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated({ValidationGroups.Inster.class}) AddCourseDto addCourseDto){
        //获取机构户信息id
        return courseBaseService.createCourseBase(1232141425L,addCourseDto);
    }

    @ApiOperation("根据id查询课程接口")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBase(@PathVariable("courseId") Long courseId){
        return courseBaseService.getCourseBaseInfo(courseId);
    }

    @ApiOperation("编辑课程接口")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated({ValidationGroups.Update.class}) EditCourseDto editCourseDto){
        return courseBaseService.modifCourseBase(1232141425L,editCourseDto);
    }


}