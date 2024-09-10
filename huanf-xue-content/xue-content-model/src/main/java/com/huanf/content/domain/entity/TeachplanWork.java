package com.huanf.content.domain.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (TeachplanWork)表实体类
 *
 * @author makejava
 * @since 2024-03-27 22:11:15
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("teachplan_work")
public class TeachplanWork  {
    //主键@TableId
    private Long id;

    //作业信息标识
    private Long workId;
    //作业标题
    private String workTitle;
    //课程计划标识
    private Long teachplanId;
    //课程标识
    private Long courseId;
    
    private Date createDate;
    
    private Long coursePubId;
}
