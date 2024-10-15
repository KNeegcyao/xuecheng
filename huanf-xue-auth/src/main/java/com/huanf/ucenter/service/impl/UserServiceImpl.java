package com.huanf.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huanf.ucenter.mapper.XcUserMapper;
import com.huanf.ucenter.model.dto.AuthParamsDto;
import com.huanf.ucenter.model.dto.XcUserExt;
import com.huanf.ucenter.model.po.XcUser;
import com.huanf.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class UserServiceImpl implements UserDetailsService {
    @Resource
    XcUserMapper xcUserMapper;
    @Resource
    //spring容器
    ApplicationContext applicationContext;

    //传入的请求认证的参数就是AuthParamDto
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AuthParamsDto authParamsDto = null;
        try {
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            log.error("认证请求数据格式不对：{}", s);
            throw new RuntimeException("请求认证的参数不符合要求");
        }
        //认证类型，有password，wx...
        String authType = authParamsDto.getAuthType();

        //根据认证类型从容器中取出指定的bean
        String beanName=authType+"_authservice";
        AuthService bean = applicationContext.getBean(beanName, AuthService.class);
        //调用统一的execute方法
        XcUserExt xcUserExt = bean.execute(authParamsDto);
        //封装xcUserExt用户信息为UserDetils
        UserDetails userPrincipal = getUserPrincipal(xcUserExt);
        return userPrincipal;
    }
    /**
     * @description 查询用户信息
     * @param xcUser  用户id，主键
     */
    public UserDetails getUserPrincipal(XcUserExt xcUser){
        String password = xcUser.getPassword();
        //权限
        String[] authorities= {"test"};
        //敏感数据制空
        xcUser.setPassword(null);
        //将用户信息转json
        String userJson = JSON.toJSONString(xcUser);
        return User.withUsername(userJson).password(password).authorities(authorities).build();
    }

}
