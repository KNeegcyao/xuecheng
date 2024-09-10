package com.huanf.content.domain.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 课程发布(CoursePublishPre)表实体类
 *
 * @author makejava
 * @since 2024-03-27 22:09:33
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("course_publish_pre")
public class CoursePublishPre  {
    //主键@TableId
    private Long id;

    //机构ID
    private Long companyId;
    //公司名称
    private String companyName;
    //课程名称
    private String name;
    //适用人群
    private String users;
    //标签
    private String tags;
    //创建人
    private String username;
    //大分类
    private String mt;
    //大分类名称
    private String mtName;
    //小分类
    private String st;
    //小分类名称
    private String stName;
    //课程等级
    private String grade;
    //教育模式
    private String teachmode;
    //课程图片
    private String pic;
    //课程介绍
    private String description;
    //课程营销信息，json格式
    private String market;
    //所有课程计划，json格式
    private String teachplan;
    //教师信息，json格式
    private String teachers;
    //提交时间
    private Date createDate;
    //审核时间
    private Date auditDate;
    //状态
    private String status;
    //备注
    private String remark;
    //收费规则，对应数据字典--203
    private String charge;
    //现价
    private Object price;
    //原价
    private Object originalPrice;
    //课程有效期天数
    private Integer validDays;
}
