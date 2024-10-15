package com.huanf.checkcode.service.impl;

import com.huanf.checkcode.model.CheckCodeParamsDto;
import com.huanf.checkcode.model.CheckCodeResultDto;
import com.huanf.checkcode.service.AbstractCheckCodeService;
import com.huanf.checkcode.service.CheckCodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("NumberCheckCodeService")
public class NumberCheckCodeServiceImpl extends AbstractCheckCodeService implements CheckCodeService {
    @Resource(name = "NumberLetterCheckCodeGenerator")
    @Override
    public void setCheckCodeGenerator(CheckCodeGenerator checkCodeGenerator) {
        this.checkCodeGenerator=checkCodeGenerator;
    }

    @Resource(name = "UUIDKeyGenerator")
    @Override
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator=keyGenerator;
    }

    @Resource(name = "RedisCheckCodeStore")
    @Override
    public void setCheckCodeStore(CheckCodeStore checkCodeStore) {
        this.checkCodeStore=checkCodeStore;
    }

    @Override
    public CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto) {
        GenerateResult generate = generate(checkCodeParamsDto, 4, "checkcode", 300);
        String key = generate.getKey();
        String code = generate.getCode();
        CheckCodeResultDto checkCodeResultDto = new CheckCodeResultDto();
        checkCodeResultDto.setKey(key);
        checkCodeResultDto.setAliasing(code);
        return checkCodeResultDto;
    }
}
