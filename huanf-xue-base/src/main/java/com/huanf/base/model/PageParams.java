package com.huanf.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: 35238
 * 功能: 分页查询的参数 通用类
 * 时间: 2024-03-28 20:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageParams {

    @ApiModelProperty("当前页码")
    private Long pageNo = 1L;

    @ApiModelProperty("每页显示多少条数据")
    private Long pageSize =10L;

}