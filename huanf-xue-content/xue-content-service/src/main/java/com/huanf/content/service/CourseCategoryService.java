package com.huanf.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huanf.domain.dto.CourseCategoryDto;
import com.huanf.domain.entity.CourseCategory;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CourseCategoryService extends IService<CourseCategory> {

    List<CourseCategoryDto> queryTreeNodes(String id);
}
