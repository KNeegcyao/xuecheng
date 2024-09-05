package com.huanf.domain.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (CourseAudit)表实体类
 *
 * @author makejava
 * @since 2024-03-27 22:02:27
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("course_audit")
public class CourseAudit  {
    @TableId
    private Long id;

    //课程id
    private Long courseId;
    //审核意见
    private String auditMind;
    //审核状态
    private String auditStatus;
    //审核人
    private String auditPeople;
    //审核时间
    private Date auditDate;
}
