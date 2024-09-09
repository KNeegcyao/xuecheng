package com.huanf.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huanf.content.domain.dto.CourseTeacherDto;
import com.huanf.content.domain.entity.CourseTeacher;

public interface TeacherService extends IService<CourseTeacher> {
    /**
     * 根据id查询教师
     * @param courseId 课程id
     * @return
     */
    CourseTeacher queryById(Long courseId);

    /**
     * 新增教师
     * @param l
     * @param courseTeacherDto
     * @return
     */
    CourseTeacher createTeacher(long l, CourseTeacherDto courseTeacherDto);

}
