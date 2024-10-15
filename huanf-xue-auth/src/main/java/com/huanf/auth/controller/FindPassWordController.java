package com.huanf.auth.controller;

import com.huanf.base.model.RestResponse;
import com.huanf.ucenter.model.dto.FindPassWordParamsDto;
import com.huanf.ucenter.service.FindPasswordService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class FindPassWordController {

    @Resource
    FindPasswordService findPasswordService;
    @ApiOperation("找回密码")
    @PostMapping("/findpassword")
    public RestResponse findPassWord(@RequestBody FindPassWordParamsDto findPassWordParamsDto){
        return findPasswordService.findPassWord(findPassWordParamsDto);
    }
}
