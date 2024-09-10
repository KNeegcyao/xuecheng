package com.huanf.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huanf.model.domain.entity.Dictionary;

import java.util.List;

/**
 * 数据字典(dictionary)表表服务接口
 *
 * @author makejava
 * @since 2024-03-31 22:14:29
 */
public interface DictionaryService extends IService<Dictionary> {

    /**
     * 查询所有数据字典内容
     * @return
     */
    List<Dictionary> queryAll();

    /**
     * 根据code查询数据字典
     * @param code -- String 数据字典Code
     * @return
     */
    Dictionary getByCode(String code);
}
