package com.huanf.content.api;

import com.huanf.content.domain.dto.CourseTeacherDto;
import com.huanf.content.domain.entity.CourseTeacher;
import com.huanf.content.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Api(value = "教师编辑接口",tags = "教师编辑接口")
public class TeacherController {
    @Resource
    TeacherService teacherService;

    @GetMapping("/courseTeacher/list/{courseId}")
    @ApiOperation("根据课程id查询教师")
    public CourseTeacher list(@PathVariable("courseId") Long courseId){
        return teacherService.queryById(courseId);
    }

    @PostMapping("/courseTeacher")
    @ApiOperation("新增教师")
    public CourseTeacher createTeacher(@RequestBody CourseTeacherDto courseTeacherDto){
        return teacherService.createTeacher(1232141425L,courseTeacherDto);

    }
}
