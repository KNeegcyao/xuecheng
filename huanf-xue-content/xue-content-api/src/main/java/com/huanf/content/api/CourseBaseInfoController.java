package com.huanf.content.api;

import com.huanf.base.model.PageParams;
import com.huanf.base.model.PageResult;
import com.huanf.content.service.CourseBaseService;
import com.huanf.domain.dto.AddCourseDto;
import com.huanf.domain.dto.CourseBaseInfoDto;
import com.huanf.domain.dto.QueryCourseParamsDto;
import com.huanf.domain.entity.CourseBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated  AddCourseDto addCourseDto){
        //获取机构户信息id
        return courseBaseService.createCourseBase(1232141425L,addCourseDto);
    }


}