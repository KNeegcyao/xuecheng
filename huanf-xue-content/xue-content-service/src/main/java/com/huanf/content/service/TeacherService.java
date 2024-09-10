package com.huanf.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huanf.content.domain.dto.AddCourseTeacherDto;
import com.huanf.content.domain.dto.EditCourseTeacherDto;
import com.huanf.content.domain.entity.CourseTeacher;

import java.util.List;

public interface TeacherService extends IService<CourseTeacher> {
    /**
     * 根据id查询教师
     * @param courseId 课程id
     * @return
     */
    List<CourseTeacher> queryById(Long courseId);

    /**
     * 新增教师信息
     * @param l
     * @param addcCurseTeacherDto
     * @return
     */
    CourseTeacher createTeacher(long l, AddCourseTeacherDto addcCurseTeacherDto);

    /**
     * 修改教师信息
     * @param l
     * @param editCourseTeacherDto
     * @return
     */
    CourseTeacher modifyTeacher(long l, EditCourseTeacherDto editCourseTeacherDto);

    /**
     * 删除教师信息
     * @param l
     * @param courseId
     * @param teacherId
     */
    void deleteTeacher(long l, Long courseId, Long teacherId);

}
