package com.huanf.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huanf.content.domain.dto.CourseCategoryDto;
import com.huanf.content.domain.entity.CourseCategory;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CourseCategoryService extends IService<CourseCategory> {

    List<CourseCategoryDto> queryTreeNodes(String id);
}
