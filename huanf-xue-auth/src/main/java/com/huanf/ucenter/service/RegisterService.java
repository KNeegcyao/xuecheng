package com.huanf.ucenter.service;

import com.huanf.base.model.RestResponse;
import com.huanf.ucenter.model.dto.RegisterParamsDto;

public interface RegisterService {
    RestResponse Register(RegisterParamsDto registerParamsDto);
}
