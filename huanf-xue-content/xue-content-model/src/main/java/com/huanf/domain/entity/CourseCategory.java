package com.huanf.domain.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 课程分类(CourseCategory)表实体类
 *
 * @author makejava
 * @since 2024-03-27 22:06:35
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("course_category")
public class CourseCategory  {
    //主键@TableId
    private String id;

    //分类名称
    private String name;
    //分类标签默认和名称一样
    private String label;
    //父结点id（第一级的父节点是0，自关联字段id）
    private String parentid;
    //是否显示
    private Integer isShow;
    //排序字段
    private Integer orderby;
    //是否叶子
    private Integer isLeaf;
}
