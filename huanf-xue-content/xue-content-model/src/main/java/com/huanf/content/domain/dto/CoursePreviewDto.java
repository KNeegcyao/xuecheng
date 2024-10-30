package com.huanf.content.domain.dto;

import com.huanf.content.domain.entity.CourseBase;
import lombok.Data;

import java.util.List;

@Data
public class CoursePreviewDto {
    //课程基本信息，课程营销信息
     CourseBaseInfoDto courseBase;
    //课程计划信息
    List<TeachplanDto> teachplans;
    //课程师资信息
}
