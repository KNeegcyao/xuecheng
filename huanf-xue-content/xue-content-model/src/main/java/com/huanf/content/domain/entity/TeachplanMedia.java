package com.huanf.content.domain.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (TeachplanMedia)表实体类
 *
 * @author makejava
 * @since 2024-03-27 22:10:59
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("teachplan_media")
public class TeachplanMedia  {
    //主键@TableId
    private Long id;

    //媒资文件id
    private String mediaId;
    //课程计划标识
    private Long teachplanId;
    //课程标识
    private Long courseId;
    //媒资文件原始名称
    private String mediaFilename;
    
    private Date createDate;
    //创建人
    private String createPeople;
    //修改人
    private String changePeople;
}
