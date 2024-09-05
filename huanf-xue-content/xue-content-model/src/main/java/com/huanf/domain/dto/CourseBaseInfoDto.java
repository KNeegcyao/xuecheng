package com.huanf.domain.dto;

import com.huanf.base.enums.CourseEnum;
import com.huanf.domain.entity.CourseBase;
import lombok.Data;

/**
 * @author: 35238
 * 功能: 课程基本信息
 * 时间: 2024-04-04 15:00
 */
@Data
public class CourseBaseInfoDto extends CourseBase {

    /**
     * 收费规则，对应数据字典
     */
    private CourseEnum charge;

    /**
     * 价格
     */
    private Float price;


    /**
     * 原价
     */
    private Float originalPrice;

    /**
     * 咨询qq
     */
    private String qq;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 电话
     */
    private String phone;

    /**
     * 有效期天数
     */
    private Integer validDays;

    /**
     * 大分类名称
     */
    private String mt;

    /**
     * 小分类名称
     */
    private String st;

}