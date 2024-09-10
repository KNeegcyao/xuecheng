package com.huanf.model.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (dictionary)表实体类
 *
 * @author makejava
 * @since 2024-03-31 22:10:27
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dictionary")
public class Dictionary implements Serializable {

    /**
     * id标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据字典名称
     */
    private String name;

    /**
     * 数据字典代码
     */
    private String code;

    /**
     * 数据字典项--json格式
            [{
                  "sd_name": "低级",
                  "sd_id": "200001",
                  "sd_status": "1"
               }, {
                  "sd_name": "中级",
                  "sd_id": "200002",
                  "sd_status": "1"
               }, {
                  "sd_name": "高级",
                  "sd_id": "200003",
                  "sd_status": "1"
               }]
     */
    private String itemValues;


}
