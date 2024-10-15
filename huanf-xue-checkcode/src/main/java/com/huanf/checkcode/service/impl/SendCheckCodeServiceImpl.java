package com.huanf.checkcode.service.impl;

import com.huanf.checkcode.model.CheckCodeParamsDto;
import com.huanf.checkcode.model.CheckCodeResultDto;
import com.huanf.checkcode.service.CheckCodeService;
import com.huanf.checkcode.service.SendCheckCodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class SendCheckCodeServiceImpl implements SendCheckCodeService {
    @Resource(name = "NumberCheckCodeService")
    private CheckCodeService numCheckCodeService;
    /**
     * 给手机发验证码
     * @param checkCodeParamsDto
     * @return
     */
    @Override
    public CheckCodeResultDto sendCheckCodeByPhone(CheckCodeParamsDto checkCodeParamsDto) {
        //获取参数，手机号还是邮箱
        String param1 = checkCodeParamsDto.getParam1();
        //生成验证码
        CheckCodeResultDto checkCodeResultDto = numCheckCodeService.generate(checkCodeParamsDto);
        //定义手机号的正则表达式
        String regexPhone="1[3-9]\\d{9}";
        //定义邮箱的正则表达式
        String regexEmail="\\w+@\\w&&[^_]{2,6}(\\.[a-zA-Z]{2,3}){1,2}";
        if(param1.matches(regexPhone)){
            System.out.println("给手机号码为："+param1+"发送验证码："+checkCodeResultDto.getAliasing());
        }else if(param1.matches(regexEmail)){
            System.out.println("给邮箱为："+param1+"发送验证码："+checkCodeResultDto.getAliasing());
        }
        return checkCodeResultDto;
    }
}
