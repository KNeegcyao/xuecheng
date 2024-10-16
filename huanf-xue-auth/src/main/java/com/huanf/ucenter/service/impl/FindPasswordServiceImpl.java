package com.huanf.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huanf.base.exception.XueChengPlusException;
import com.huanf.base.model.RestResponse;
import com.huanf.ucenter.feignclient.CheckCodeClient;
import com.huanf.ucenter.mapper.XcUserMapper;
import com.huanf.ucenter.model.dto.FindPassWordParamsDto;
import com.huanf.ucenter.model.po.XcUser;
import com.huanf.ucenter.service.FindPasswordService;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class FindPasswordServiceImpl implements FindPasswordService {
    @Resource
    CheckCodeClient checkCodeClient;
    @Resource
    XcUserMapper xcUserMapper;
    @Resource
    PasswordEncoder passwordEncoder;
    @Override
    public RestResponse findPassWord(FindPassWordParamsDto findPassWordParamsDto) {
        String checkcodekey = findPassWordParamsDto.getCheckcodekey();
        String checkcode = findPassWordParamsDto.getCheckcode();
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if(!verify){
             XueChengPlusException.cast("验证码不一致");
        }
        String password = findPassWordParamsDto.getPassword();
        String confirmpwd = findPassWordParamsDto.getConfirmpwd();
        if(!password.equals(confirmpwd)){
            XueChengPlusException.cast("密码前后不一致");
        }
        String email = findPassWordParamsDto.getEmail();
        String cellphone = findPassWordParamsDto.getCellphone();
        XcUser xcUser=null;
        if(!(cellphone ==null)) {
            //手机号查询
             xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>()
                    .eq(XcUser::getCellphone, cellphone));
        }else{
            //邮箱查询
            xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>()
                    .eq(XcUser::getEmail,email ));
        }
        if(xcUser==null){
            XueChengPlusException.cast("用户不存在");
        }
        //给密码加密
        String encode = passwordEncoder.encode(password);
        xcUser.setPassword(encode);
        xcUser.setUpdateTime(LocalDateTime.now());
        int i = xcUserMapper.updateById(xcUser);
        if(i==0){
            XueChengPlusException.cast("修改用户信息失败");
        }

        //构建响应
        RestResponse<String> restResponse = new RestResponse<>();
        restResponse.setCode(200);
        restResponse.setMsg("找回成功");
        return restResponse;

    }
}
