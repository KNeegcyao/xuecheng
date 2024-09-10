package com.huanf.content.domain.dto;


import com.huanf.content.domain.entity.Teachplan;
import com.huanf.content.domain.entity.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 *
 */
@Data
@ToString
public class TeachplanDto extends Teachplan {
    //小章节列表
    private List<TeachplanDto> teachPlanTreeNodes;

    //与媒资管理的信息
    private TeachplanMedia teachplanMedia;

}
