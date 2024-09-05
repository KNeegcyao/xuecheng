package com.huanf.content.api;

import com.huanf.content.service.CourseCategoryService;
import com.huanf.domain.dto.CourseCategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Result;
import java.util.List;

/**
 * 课程分类接口
 */

@RestController
public class CourseCategoryController {
    @Autowired
    private CourseCategoryService courseCategoryService;
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryDto> queryTreeNodes(){
        return courseCategoryService.queryTreeNodes("1");
    }
}
