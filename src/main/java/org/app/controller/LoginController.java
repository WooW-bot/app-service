package org.app.controller;

import org.app.common.ResponseVO;
import org.app.model.req.AuthReq;
import org.app.model.req.SendSmsReq;
import org.app.service.AuthService;
import org.app.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录注册控制器
 *
 * @author Parker
 * @date 12/21/25
 */
@RestController
@RequestMapping("v1")
public class LoginController {

    @Autowired
    AuthService authService;

    @Autowired
    SmsService smsService;

    /**
     * 统一认证接口 (登录/注册)
     * 新接口，推荐使用
     */
    @RequestMapping("/auth")
    public ResponseVO auth(@RequestBody @Validated AuthReq req) {
        return authService.auth(req);
    }

    /**
     * 发送短信验证码
     */
    @RequestMapping("/sms/send")
    public ResponseVO sendSms(@RequestBody @Validated SendSmsReq req) {
        return smsService.sendSmsCode(req.getCountryCode(), req.getMobile());
    }
}
