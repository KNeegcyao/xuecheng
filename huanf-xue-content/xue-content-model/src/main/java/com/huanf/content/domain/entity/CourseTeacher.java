package com.huanf.content.domain.entity;

import java.time.LocalDateTime;
import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression.DateTime;

/**
 * 课程-教师关系表(CourseTeacher)表实体类
 *
 * @author makejava
 * @since 2024-03-27 22:09:51
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("course_teacher")
public class CourseTeacher  {
    //主键@TableId
    private Long id;

    //课程标识
    private Long courseId;
    //教师标识
    private String teacherName;
    //教师职位
    private String position;
    //教师简介
    private String introduction;
    //照片
    private String photograph;
    //创建时间
    private Date createDate;
}
