package com.huanf.domain.entity;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 课程营销信息(CourseMarket)表实体类
 *
 * @author makejava
 * @since 2024-03-27 22:07:50
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("course_market")
public class CourseMarket  {
    //主键，课程id@TableId
    private Long id;

    //收费规则，对应数据字典
    private String charge;
    //现价
    private Float price;
    //原价
    private Float originalPrice;
    //咨询qq
    private String qq;
    //微信
    private String wechat;
    //电话
    private String phone;
    //有效期天数
    private Integer validDays;
}
