package com.huanf.content.domain.dto;

import com.huanf.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.text.Position;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="AddCourseTeacherDto", description="新增教师基本信息")
public class AddCourseTeacherDto {

    @NotEmpty(message = "课程id不能为空")
    @ApiModelProperty(value = "课程id", required = true)
    private Long courseId;

    @NotEmpty(groups = {ValidationGroups.Inster.class},message = "添加教师名称不能为空")
    @NotEmpty(groups = {ValidationGroups.Update.class},message = "修改教师名称不能为空")
    @ApiModelProperty(value = "教师名称", required = true)
    private String teacherName;

    @ApiModelProperty(value = "教师职位",required = true)
    @Size(message = "教师职位内容过少",min=1)
    private String position;

    @ApiModelProperty(value = "教师介绍",required = true)
    @Size(message = "教师介绍内容过少",groups = {ValidationGroups.Inster.class},min=10)
    private String introduction;

    @ApiModelProperty(value="教师照片",required = false)
    private String photograph;

    @ApiModelProperty(value = "创建时间",required = false)
    private LocalDateTime createTime;
}
