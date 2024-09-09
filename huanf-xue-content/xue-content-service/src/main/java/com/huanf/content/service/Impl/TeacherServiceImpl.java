package com.huanf.content.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huanf.content.domain.dto.CourseTeacherDto;
import com.huanf.content.domain.entity.CourseTeacher;
import com.huanf.content.mapper.TeacherMapper;
import com.huanf.content.service.TeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.xuecheng.base.exception.XueChengPlusException;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, CourseTeacher> implements TeacherService {
    @Resource
    TeacherMapper teacherMapper;
    /**
     * 根据课程id查询教师
     * @param courseId
     * @return
     */
    @Override
    public CourseTeacher queryById(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper=queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        CourseTeacher teacher = teacherMapper.selectOne(queryWrapper);
        if(teacher==null){
            throw new XueChengPlusException("教师为空");
        }
        return teacher;
    }

    /**
     * 新增教师
     * @param companyId 机构id
     * @param courseTeacherDto
     * @return
     */
    @Override
    public CourseTeacher createTeacher(long companyId, CourseTeacherDto courseTeacherDto) {
        CourseTeacher teacher = new CourseTeacher();
        BeanUtils.copyProperties(courseTeacherDto,teacher);
        teacher.setCreateDate(new Date());
        int i = teacherMapper.insert(teacher);
        if(i<=0){
            throw new XueChengPlusException("添加教师失败");
        }
        return teacher;
    }
}
