package com.huanf.content.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.text.Position;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseTeacherDto {
    private String courseId;
    private String teacherName;
    private String position;
    private String introduction;
}
