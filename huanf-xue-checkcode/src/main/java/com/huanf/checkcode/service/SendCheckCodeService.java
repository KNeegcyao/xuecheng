package com.huanf.checkcode.service;

import com.huanf.checkcode.model.CheckCodeParamsDto;
import com.huanf.checkcode.model.CheckCodeResultDto;

public interface SendCheckCodeService {

    CheckCodeResultDto sendCheckCodeByPhone(CheckCodeParamsDto checkCodeParamsDto);
}
