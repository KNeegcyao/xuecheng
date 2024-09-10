package com.huanf.content.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huanf.base.exception.ValidationGroups;
import com.huanf.content.domain.dto.AddCourseTeacherDto;
import com.huanf.content.domain.dto.EditCourseTeacherDto;
import com.huanf.content.domain.entity.CourseTeacher;
import com.huanf.content.mapper.CourseBaseMapper;
import com.huanf.content.mapper.TeacherMapper;
import com.huanf.content.service.TeacherService;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.xuecheng.base.exception.XueChengPlusException;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, CourseTeacher> implements TeacherService {
    @Resource
    TeacherMapper teacherMapper;

    @Resource
    CourseBaseMapper courseBaseMapper;
    /**
     * 根据课程id查询教师
     * @param courseId
     * @return
     */
    @Override
    public List<CourseTeacher> queryById(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper=queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        List<CourseTeacher> courseTeachers = teacherMapper.selectList(queryWrapper);
        if(courseTeachers==null){
            XueChengPlusException.cast("教师为空");
        }
        return courseTeachers;
    }

    /**
     * 新增教师
     * @param companyId 机构id
     * @param addCourseTeacherDto
     * @return
     */
    @Override
    public CourseTeacher createTeacher(long companyId, AddCourseTeacherDto addCourseTeacherDto) {
        CourseTeacher teacher = new CourseTeacher();
        if(addCourseTeacherDto.getCourseId()==null){
            XueChengPlusException.cast("课程号不能为空");
        }
        //判断机构是否合法
        boolean success = checkCompanyId(companyId, addCourseTeacherDto.getCourseId());
        if(!success){
            XueChengPlusException.cast("只允许向机构自己的课程中添加老师");
        }
        //判断是否重复添加
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper=queryWrapper.eq(CourseTeacher::getCourseId,addCourseTeacherDto.getCourseId())
                .eq(CourseTeacher::getTeacherName,addCourseTeacherDto.getTeacherName());
        Integer existingCount = teacherMapper.selectCount(queryWrapper);
        if(existingCount>0){
            XueChengPlusException.cast("该老师已经存在，请勿重复添加");
        }
        BeanUtils.copyProperties(addCourseTeacherDto,teacher);
        teacher.setCreateDate(new Date());
        int i = teacherMapper.insert(teacher);
        if(i<=0){
            XueChengPlusException.cast("添加教师失败");
        }
        return teacher;
    }

    /**
     * 修改教师信息
     * @param l
     * @param editCourseTeacherDto
     * @return
     */
    @Override
    public CourseTeacher modifyTeacher(long companyId, EditCourseTeacherDto editCourseTeacherDto) {
        //判断机构是否合法
        boolean success = checkCompanyId(companyId, editCourseTeacherDto.getCourseId());
        if(!success){
            XueChengPlusException.cast("只允许向机构自己的课程中修改老师");
        }
        Long id = editCourseTeacherDto.getId();
        CourseTeacher teacher = teacherMapper.selectById(id);
        if(teacher==null){
            XueChengPlusException.cast("修改教师用户不存在");
        }
        CourseTeacher newteacher = new CourseTeacher();
        BeanUtils.copyProperties(editCourseTeacherDto,newteacher);
        newteacher.setCreateDate(new Date());
        int i = teacherMapper.updateById(newteacher);
        if(i<=0){
            XueChengPlusException.cast("修改教师失败");
        }
        return newteacher;
    }

    /**
     * 删除教师信息
     * @param l
     * @param courseId
     * @param teacherId
     */
    @Override
    public void deleteTeacher(long companyId, Long courseId, Long teacherId) {
        //判断机构是否合法
        boolean success = checkCompanyId(companyId, courseId);
        if(!success){
            XueChengPlusException.cast("只允许向机构自己的课程中删除老师");
        }
        CourseTeacher teacher = teacherMapper.selectById(teacherId);
        if(teacher==null){
            XueChengPlusException.cast("删除教师用户不存在");
        }
        int i = teacherMapper.deleteById(teacherId);
        if(i<=0){
            XueChengPlusException.cast("删除教师失败");
        }
    }

    /**
     * 检查机构id符合
     * @param companyId
     * @param courseId
     */
    private boolean checkCompanyId(long companyId, long courseId) {
        Long previousCompanyId = courseBaseMapper.selectById(courseId).getCompanyId();
        if(companyId!=previousCompanyId){
            return false;
        }
        return true;
    }
}
