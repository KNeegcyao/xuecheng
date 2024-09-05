package com.huanf.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: 35238
 * 功能: 课程查询参数
 * 时间: 2024-03-28 20:45
 */
@Data
public class QueryCourseParamsDto {

    @ApiModelProperty("审核状态")
    private String auditStatus;

    @ApiModelProperty("课程名称")
    private String courseName;

    @ApiModelProperty("发布状态")
    private String publishStatus;

}