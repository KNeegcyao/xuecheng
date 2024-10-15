package com.huanf.ucenter.service;

import com.huanf.base.model.RestResponse;
import com.huanf.ucenter.model.dto.FindPassWordParamsDto;

public interface FindPasswordService {
    RestResponse findPassWord(FindPassWordParamsDto findPassWordParamsDto);
}
