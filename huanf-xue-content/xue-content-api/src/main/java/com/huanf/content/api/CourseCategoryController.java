package com.huanf.content.api;

import com.huanf.content.domain.dto.CourseCategoryDto;
import com.huanf.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.xml.transform.Result;
import java.util.List;

/**
 * 课程分类接口
 */

@RestController
public class CourseCategoryController {
    @Resource
    private CourseCategoryService courseCategoryService;
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryDto> queryTreeNodes(){
        return courseCategoryService.queryTreeNodes("1");
    }
}
