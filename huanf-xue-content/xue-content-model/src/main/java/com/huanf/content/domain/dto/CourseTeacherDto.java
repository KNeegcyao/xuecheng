package com.huanf.content.domain.dto;

import com.huanf.base.exception.ValidationGroups;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.text.Position;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="CourseTeacherDto", description="新增教师基本信息")
public class CourseTeacherDto {

    private String id;
    private String courseId;
    @NotEmpty(groups = {ValidationGroups.Inster.class},message = "添加教师名称不能为空")
    @NotEmpty(groups = {ValidationGroups.Update.class},message = "修改教师名称不能为空")
    @ApiModelProperty(value = "教师名称", required = true)
    private String teacherName;

    private String position;
    @ApiModelProperty(value = "教师介绍")
    private String introduction;
}
