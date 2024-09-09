package com.huanf.content.service;

import com.huanf.content.domain.dto.SaveTeachplanDto;
import com.huanf.content.domain.dto.TeachplanDto;

import java.util.List;

public interface TeachplanService {
    /**
     *根据课程id查询课程计划
     * @param courseId
     * @return
     */
    public List<TeachplanDto> findTeachplanTree(long courseId);

    /**
     * 课程计划的创建或修改
     * @param saveTeachplanDto
     */
    void SaveTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * 删除课程计划
     * @param teachplanId
     */
    void deleteTeachplan(Long teachplanId);

    /**
     * 下移课程计划
     * @param teachplanId
     */
    void movedown(String down,Long teachplanId);
}
