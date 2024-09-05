package com.huanf.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.huanf.base.enums.CourseEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 课程基本信息(CourseBase)表实体类
 *
 * @author makejava
 * @since 2024-03-27 22:05:37
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("course_base")
public class CourseBase  {
    //主键@TableId
    private Long id;

    //机构ID
    private Long companyId;
    //机构名称
    private String companyName;
    //课程名称
    private String name;
    //适用人群
    private String users;
    //课程标签
    private String tags;
    //大分类
    private String mt;
    //小分类
    private String st;
    //课程等级
    private String grade;
    //教育模式(common普通，record 录播，live直播等）
    private String teachmode;
    //课程介绍
    private String description;
    //课程图片
    private String pic;
    //创建时间
    private Date createDate;
    //修改时间
    private Date changeDate;
    //创建人
    private String createPeople;
    //更新人
    private String changePeople;
    //审核状态
    private CourseEnum auditStatus;
    //课程发布状态 未发布  已发布 下线
    private CourseEnum status;

    //课程是否需要收费。下面这个注解表示charge字段在course_base表不存在
    @TableField(exist = false)
    private CourseEnum charge;
}