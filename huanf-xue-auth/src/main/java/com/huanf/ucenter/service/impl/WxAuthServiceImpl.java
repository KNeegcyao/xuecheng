package com.huanf.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huanf.ucenter.mapper.XcUserMapper;
import com.huanf.ucenter.mapper.XcUserRoleMapper;
import com.huanf.ucenter.model.dto.AuthParamsDto;
import com.huanf.ucenter.model.dto.XcUserExt;
import com.huanf.ucenter.model.po.XcUser;
import com.huanf.ucenter.model.po.XcUserRole;
import com.huanf.ucenter.service.AuthService;
import com.huanf.ucenter.service.WxAuthService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService, WxAuthService {
    @Value("${weixin.appid}")
    String appid;
    @Value("${weixin.secret}")
    String secret;
    @Resource
    XcUserMapper xcUserMapper;
    @Resource
    XcUserRoleMapper xcUserRoleMapper;
    @Resource
    RestTemplate restTemplate;
    @Resource
    WxAuthServiceImpl currentPorxy;
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        // 账号
        String username = authParamsDto.getUsername();
        //查询数据库
        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user, xcUserExt);
        return xcUserExt;
    }

    @Override
    public XcUser wxAuth(String code) {
        //申请令牌
        Map<String, String> access_token_map = getAccess_token(code);
        //访问令牌
        String access_token = access_token_map.get("access_token");
        String openid = access_token_map.get("openid");

        // 携带令牌查询用户信息
        Map<String, String> userinfo = getUserinfo(access_token, openid);
        // 保存用户信息到数据库
        XcUser xcUser = currentPorxy.addWxUser(userinfo);
        return xcUser;
    }

    @Transactional
    public XcUser addWxUser(Map<String,String> userInfo_map){
        String unionid = userInfo_map.get("unionid");
        //根据unionid查询用户信息
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getWxUnionid, unionid));
        if(xcUser!=null){
            return xcUser;
        }
        //向数据库新增记录
        String userId = UUID.randomUUID().toString();
        xcUser = new XcUser();
        xcUser.setId(userId);
        xcUser.setWxUnionid(unionid);
        //记录从微信得到的昵称
        xcUser.setNickname(userInfo_map.get("nickname"));
        xcUser.setUserpic(userInfo_map.get("headimgurl"));
        xcUser.setName(userInfo_map.get("nickname"));
        xcUser.setUsername(unionid);
        xcUser.setPassword(unionid);
        xcUser.setUtype("101001");//学生类型
        xcUser.setStatus("1");//用户状态
        xcUser.setCreateTime(LocalDateTime.now());
        xcUserMapper.insert(xcUser);
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        xcUserRole.setRoleId("17");//学生角色
        xcUserRoleMapper.insert(xcUserRole);
        return xcUser;
    }


    /**
     * 携带授权码申请令牌
     * https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code
     * @param code 授权码
     * @return
     */
    /**
     * 申请访问令牌,响应示例
     {
     "access_token":"ACCESS_TOKEN",
     "expires_in":7200,
     "refresh_token":"REFRESH_TOKEN",
     "openid":"OPENID",
     "scope":"SCOPE",
     "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
     }
     */
    private Map<String,String> getAccess_token(String code){
        String url_template = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        //请求路径
        String url = String.format(url_template, appid, secret, code);
        //远程调用url
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        //获取响应的结果
        String result = exchange.getBody();
        //将result转成map
        Map<String,String> map = JSON.parseObject(result, Map.class);
        return map;
    }
    /**获取用户信息，示例如下：
     {
     "openid":"OPENID",
     "nickname":"NICKNAME",
     "sex":1,
     "province":"PROVINCE",
     "city":"CITY",
     "country":"COUNTRY",
     "headimgurl": "https://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
     "privilege":[
     "PRIVILEGE1",
     "PRIVILEGE2"
     ],
     "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
     }
     */
    private Map<String,String> getUserinfo(String access_token,String openid) {
        String url_template = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        String url = String.format(url_template, access_token, openid);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        //获取响应的结果
        //防止乱码进行转码
        String result = new String(exchange.getBody().getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
        //将result转成map
        Map<String,String> map = JSON.parseObject(result, Map.class);
        return map;
    }
}
