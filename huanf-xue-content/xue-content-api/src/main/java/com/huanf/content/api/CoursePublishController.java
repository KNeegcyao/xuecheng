package com.huanf.content.api;

import com.huanf.content.domain.dto.CoursePreviewDto;
import com.huanf.content.service.CoursePublishService;
import io.swagger.annotations.ApiModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * 课程上传接口
 */
@Controller
@ApiModel
public class CoursePublishController {

    @Resource
    CoursePublishService coursePublishService;
    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId){

        ModelAndView modelAndView = new ModelAndView();
        //查询课程的信息作为模型数据
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
        modelAndView.addObject("model",coursePreviewInfo);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }
}