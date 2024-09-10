package com.huanf.system.api;

import com.huanf.model.domain.entity.Dictionary;
import com.huanf.system.service.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: 35238
 * 功能: 系统信息编辑接口
 * 时间: 2024-03-31 22:11:19
 */
@RestController
public class DictionaryController  {

    @Autowired
    private DictionaryService dictionaryService;

    @GetMapping("/dictionary/all")
    public List<Dictionary> queryAll() {
        System.out.println("你好");
        return dictionaryService.queryAll();
    }

    @GetMapping("/dictionary/code/{code}")
    public Dictionary getByCode(@PathVariable String code) {
        return dictionaryService.getByCode(code);
    }
}
