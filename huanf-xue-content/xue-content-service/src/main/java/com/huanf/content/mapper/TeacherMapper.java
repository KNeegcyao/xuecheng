package com.huanf.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanf.content.domain.entity.CourseTeacher;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeacherMapper extends BaseMapper<CourseTeacher> {
}
