package com.huanf.content.domain.dto;

import com.huanf.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.internal.engine.groups.ValidationOrderGenerator;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author: 35238
 * 功能: 添加课程
 * 时间: 2024-04-04 15:02
 *
 * <dependency>
 *   <groupId>org.springframework.boot</groupId>

 *   <artifactId>spring-boot-starter-validation</artifactId>

 *   <version>2.3.7.RELEASE</version>

 * </dependency>

 */
@Data
@ApiModel(value="AddCourseDto", description="新增课程基本信息")
public class AddCourseDto{
    @NotEmpty(groups = {ValidationGroups.Inster.class},message = "添加课程名称不能为空")
    @NotEmpty(groups = {ValidationGroups.Update.class},message = "修改课程名称不能为空")
    @ApiModelProperty(value = "课程名称", required = true)
    private String name;

    @NotEmpty(message = "适用人群不能为空")
    @Size(max = 20, message = "适用人群内容过大")//注意这里校验的就是字符串位数，而不是数值大小
    //@Max(value = 20, message = "适用人群过少，请最低填入20") //如果要校验字符串的数值大小，用@Max或@Min，别用@Size
    @ApiModelProperty(value = "适用人群", required = true)
    private String users;

    @ApiModelProperty(value = "课程标签")
    private String tags;

    @NotEmpty(message = "课程分类不能为空")
    @ApiModelProperty(value = "大分类", required = true)
    private String mt;

    @NotEmpty(message = "课程分类不能为空")
    @ApiModelProperty(value = "小分类", required = true)
    private String st;

    @NotEmpty(message = "课程等级不能为空")
    @ApiModelProperty(value = "课程等级", required = true)
    private String grade;

    @ApiModelProperty(value = "教学模式（普通，录播，直播等）", required = true)
    private String teachmode;

    @ApiModelProperty(value = "课程介绍")
    @Size(message = "课程介绍内容过少",min=10)
    private String description;

    @ApiModelProperty(value = "课程图片", required = true)
    private String pic;

    @NotEmpty(message = "收费规则不能为空")
    @ApiModelProperty(value = "收费规则。201000 表示免费，201001 表示收费", required = true)
    //这里别写枚举类型，只有跟数据库交互的字段我们才写枚举，但是这里只是DTO，是接收前端传过来的参数，写String是最佳的
    private String charge;

    @ApiModelProperty(value = "价格")
    private Float price;
    @ApiModelProperty(value = "原价")
    private Float originalPrice;

    @ApiModelProperty(value = "qq")
    private String qq;

    @ApiModelProperty(value = "微信")
    private String wechat;
    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "有效期")
    private Integer validDays;

}