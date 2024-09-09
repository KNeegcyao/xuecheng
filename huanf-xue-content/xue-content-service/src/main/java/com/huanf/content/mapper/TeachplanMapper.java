package com.huanf.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanf.content.domain.dto.TeachplanDto;
import com.huanf.content.domain.entity.Teachplan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TeachplanMapper extends BaseMapper<Teachplan> {
    //课程计划查询
     List<TeachplanDto> selectTreeNodes(Long courseId);
}
