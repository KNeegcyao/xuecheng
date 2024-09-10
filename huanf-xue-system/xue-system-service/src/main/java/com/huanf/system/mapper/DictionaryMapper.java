package com.huanf.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanf.model.domain.entity.Dictionary;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据字典(dictionary)表数据库访问层
 *
 * @author makejava
 * @since 2024-03-31 22:13:34
 */
@Mapper
public interface DictionaryMapper extends BaseMapper<Dictionary> {

}
