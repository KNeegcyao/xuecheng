package com.huanf.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huanf.base.exception.XueChengPlusException;
import com.huanf.base.model.RestResponse;
import com.huanf.ucenter.feignclient.CheckCodeClient;
import com.huanf.ucenter.mapper.XcUserMapper;
import com.huanf.ucenter.mapper.XcUserRoleMapper;
import com.huanf.ucenter.model.dto.RegisterParamsDto;
import com.huanf.ucenter.model.po.XcUser;
import com.huanf.ucenter.model.po.XcUserRole;
import com.huanf.ucenter.service.RegisterService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Resource
    CheckCodeClient checkCodeClient;
    @Resource
    XcUserMapper xcUserMapper;
    @Resource
    XcUserRoleMapper xcUserRoleMapper;
    @Resource
    PasswordEncoder passwordEncoder;

    @Override
    public RestResponse Register(RegisterParamsDto registerParamsDto) {
        String checkcode = registerParamsDto.getCheckcode();
        String checkcodekey = registerParamsDto.getCheckcodekey();
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if(!verify){
            XueChengPlusException.cast("验证码错误");
        }
        String password = registerParamsDto.getPassword();
        String confirmpwd = registerParamsDto.getConfirmpwd();
        if(!password.equals(confirmpwd)){
            XueChengPlusException.cast("密码前后不一致");
        }
        String cellphone = registerParamsDto.getCellphone();
        String email = registerParamsDto.getEmail();
        if(cellphone==null&&email==null){
            XueChengPlusException.cast("手机号和邮箱不能都为空");
        }
        XcUser xcUser=null;
        if(email!=null){
            xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getEmail,email));
        }else if(cellphone!=null){
            xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getCellphone,cellphone));
        }

        if(xcUser!=null){
            XueChengPlusException.cast("该用户已经存在");
        }

        //向用户表，用户角色关系表添加数据
        //写入用户表
         xcUser = new XcUser();
        BeanUtils.copyProperties(registerParamsDto,xcUser);
        String encode = passwordEncoder.encode(xcUser.getPassword()); //给密码加密
        xcUser.setPassword(encode);
        xcUser.setUtype("101001");
        xcUser.setStatus("1");
        xcUser.setName("学生"+ UUID.randomUUID());
        xcUser.setCreateTime(LocalDateTime.now());
        xcUserMapper.insert(xcUser);
        //写入用户角色关系表
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setUserId(xcUser.getId());
        xcUserRole.setRoleId("17");  //学生
        xcUserRole.setCreateTime(LocalDateTime.now());
        xcUserRoleMapper.insert(xcUserRole);
        return RestResponse.success(true,"注册成功");



    }
}
