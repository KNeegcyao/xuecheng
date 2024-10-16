package com.huanf.ucenter.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterParamsDto extends FindPassWordParamsDto{
    private String nickname;
    private String username;
}
