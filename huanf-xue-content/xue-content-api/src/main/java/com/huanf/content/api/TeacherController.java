package com.huanf.content.api;

import com.huanf.base.exception.ValidationGroups;
import com.huanf.content.domain.dto.AddCourseTeacherDto;
import com.huanf.content.domain.dto.EditCourseTeacherDto;
import com.huanf.content.domain.entity.CourseTeacher;
import com.huanf.content.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(value = "教师编辑接口",tags = "教师编辑接口")
public class TeacherController {
    @Resource
    TeacherService teacherService;

    @GetMapping("/courseTeacher/list/{courseId}")
    @ApiOperation("根据课程id查询教师")
    public List<CourseTeacher> list(@PathVariable("courseId") Long courseId){
        return teacherService.queryById(courseId);
    }

    @PostMapping("/courseTeacher")
    @ApiOperation("新增教师信息")
    public CourseTeacher createTeacher(@RequestBody @Validated ({ValidationGroups.Inster.class}) AddCourseTeacherDto addCourseTeacherDto){
        return teacherService.createTeacher(1232141425L,addCourseTeacherDto);
    }

    @PutMapping("/courseTeacher")
    @ApiOperation("修改教师信息")
    public CourseTeacher modifyTeacher(@RequestBody EditCourseTeacherDto editCourseTeacherDto){
        return teacherService.modifyTeacher(1232141425L,editCourseTeacherDto);
    }

    @DeleteMapping("/courseTeacher/course/{courseId}/{teacher_id}")
    @ApiOperation("删除教师信息")
    public void deleteTeacher(@PathVariable Long courseId,@PathVariable Long teacher_id){
        teacherService.deleteTeacher(1232141425L,courseId,teacher_id);

    }
}
