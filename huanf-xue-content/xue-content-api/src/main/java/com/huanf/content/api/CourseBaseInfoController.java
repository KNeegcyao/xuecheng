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
import com.huanf.content.service.TeacherService;
import com.huanf.content.util.SecurityUtil;
import com.huanf.content.util.SecurityUtil.XcUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")//指定权限标识符
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParams){
        //当前登录的用户
        XcUser user = SecurityUtil.getUser();
        //用户所属机构id
        Long companyId=null;
        if(StringUtils.isNotEmpty(user.getCompanyId())){
            companyId=Long.parseLong(user.getCompanyId());
        }
        return courseBaseService.queryCourseBaseList(companyId,pageParams,queryCourseParams);
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
        //取出当前用户信息
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(principal);
        return courseBaseService.getCourseBaseInfo(courseId);
    }

    @ApiOperation("编辑课程接口")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated({ValidationGroups.Update.class}) EditCourseDto editCourseDto){
        return courseBaseService.modifCourseBase(1232141425L,editCourseDto);
    }


    @ApiOperation("删除课程接口")
    @DeleteMapping("/course/{courseId}")
    public void deleteCourseBase(@PathVariable Long courseId){
        courseBaseService.deleteCourseBase(1232141425L,courseId);

    }
}