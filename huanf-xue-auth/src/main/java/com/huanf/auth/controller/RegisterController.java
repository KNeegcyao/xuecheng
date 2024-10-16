package com.huanf.auth.controller;

import com.huanf.base.model.RestResponse;
import com.huanf.ucenter.model.dto.RegisterParamsDto;
import com.huanf.ucenter.service.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
public class RegisterController {
    @Resource
    RegisterService registerService;

    @PostMapping("register")
    public RestResponse Register(@RequestBody RegisterParamsDto registerParamsDto){
        return registerService.Register(registerParamsDto);
    }
}
