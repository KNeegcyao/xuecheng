package com.huanf.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huanf.model.domain.entity.Dictionary;
import com.huanf.system.mapper.DictionaryMapper;
import com.huanf.system.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据字典(dictionary)表服务实现类
 *
 * @author makejava
 * @since 2024-03-31 22:15:06
 */
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

    @Autowired
    DictionaryService dictionaryService;

    @Override
    public List<Dictionary> queryAll() {
        List<Dictionary> list = dictionaryService.list();
        return list;
    }

    @Override
    public Dictionary getByCode(String code) {
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dictionary::getCode, code);
        Dictionary dictionary = dictionaryService.getOne(queryWrapper);
        return dictionary;
    }
}
