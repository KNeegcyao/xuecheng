package com.huanf.content.domain.dto;

import com.huanf.content.domain.entity.CourseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseCategoryDto extends CourseCategory implements java.io.Serializable{
    //子节点
    private List<CourseCategoryDto> childrenTreeNodes;

}
