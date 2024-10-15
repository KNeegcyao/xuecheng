package com.huanf.ucenter.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 找回密码封装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindPassWordParamsDto {
    private String cellphone; //手机号
    private String email;  //邮箱
    private String checkcodekey;//验证码key
    private String checkcode; //用户输入的验证码
    private String confirmpwd; //确认用户密码
    private String password; //用户密码
}
