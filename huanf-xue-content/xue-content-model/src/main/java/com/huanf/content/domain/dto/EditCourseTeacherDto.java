package com.huanf.content.domain.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "EditCourseTeacherDto ",description = "修改教师基本信息")
public class EditCourseTeacherDto extends AddCourseTeacherDto{
    @ApiModelProperty(value = "教师id", required = true)
    private Long id;
}
