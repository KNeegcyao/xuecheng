package com.huanf.learning.service;

import com.huanf.content.domain.entity.CoursePublish;
import com.huanf.learning.model.dto.XcChooseCourseDto;
import com.huanf.learning.model.dto.XcCourseTablesDto;
import com.huanf.learning.model.po.XcChooseCourse;
import com.huanf.learning.model.po.XcCourseTables;

public interface MyCourseTableService {
    /**
     * 添加选课
     * @param userId
     * @param courseId
     * @return
     */
    XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    /**
     * @param userId
     * @param courseId
     * @return XcCourseTablesDto 学习资格状态 [{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
     * @description 判断学习资格
     */
    XcCourseTablesDto getLearningStatus(String userId, Long courseId);


    XcChooseCourse addFreeCourse(String userId, CoursePublish coursePublish);

    XcChooseCourse addChargeCourse(String userId, CoursePublish coursePublish);

    //添加到我的课程表、
    XcCourseTables addCourseTables(XcChooseCourse xcChooseCourse);

    XcCourseTables getXcCourseTables(String userId, Long courseId);
}
