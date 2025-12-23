package org.app.service.impl;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.app.common.ResponseVO;
import org.app.config.AppConfig;
import org.app.dao.User;
import org.app.enums.ErrorCode;
import org.app.model.req.AuthReq;
import org.app.model.req.RegisterReq;
import org.app.model.resp.LoginResp;
import org.app.service.AuthService;
import org.app.service.SmsService;
import org.app.service.UserService;
import org.app.utils.JwtUtil;
import org.app.utils.PasswordEncoder;
import org.app.utils.SigAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 统一认证服务实现
 *
 * @author Parker
 * @date 12/23/25
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private AppConfig appConfig;

    @Override
    public ResponseVO auth(AuthReq req) {
        // 1. 参数校验: password 和 smsCode 至少提供一个
        if (!req.isPasswordAuth() && !req.isSmsCodeAuth()) {
            return ResponseVO.errorResponse(ErrorCode.AUTH_PARAM_ERROR);
        }

        // 2. 根据认证方式路由
        if (req.isPasswordAuth()) {
            return authenticateByPassword(req);
        } else {
            return authenticateBySmsCode(req);
        }
    }

    /**
     * 密码认证 (仅登录)
     */
    private ResponseVO authenticateByPassword(AuthReq req) {
        // 1. 根据手机号查询用户
        ResponseVO userResp = userService.getUserByMobile(req.getCountryCode(), req.getMobile());
        if (!userResp.isSuccess()) {
            return userResp;
        }

        User user = (User) userResp.getData();
        if (user == null) {
            log.warn("[密码登录] 用户不存在 - 手机号: {}{}", req.getCountryCode(), req.getMobile());
            return ResponseVO.errorResponse(ErrorCode.USER_NOT_EXIST);
        }

        // 2. 验证密码
        if (user.getPassword() == null || !PasswordEncoder.matches(req.getPassword(), user.getPassword())) {
            log.warn("[密码登录] 密码错误 - 用户ID: {}", user.getUserId());
            return ResponseVO.errorResponse(ErrorCode.PASSWORD_ERROR);
        }

        // 3. 登录成功
        log.info("[密码登录] 登录成功 - 用户ID: {}, 手机号: {}{}",
                user.getUserId(), req.getCountryCode(), req.getMobile());
        return ResponseVO.successResponse(buildLoginResponse(user));
    }

    /**
     * 验证码认证 (登录或注册)
     */
    private ResponseVO authenticateBySmsCode(AuthReq req) {
        // 1. 验证短信验证码
        boolean isValid = smsService.verifySmsCode(req.getCountryCode(), req.getMobile(), req.getSmsCode());
        if (!isValid) {
            log.warn("[验证码认证] 验证码错误 - 手机号: {}{}", req.getCountryCode(), req.getMobile());
            return ResponseVO.errorResponse(ErrorCode.SMS_CODE_ERROR);
        }

        // 2. 查询用户是否存在
        ResponseVO userResp = userService.getUserByMobile(req.getCountryCode(), req.getMobile());
        if (!userResp.isSuccess()) {
            return userResp;
        }

        User user = (User) userResp.getData();

        // 3. 用户存在 -> 登录; 用户不存在 -> 注册
        if (user != null) {
            log.info("[验证码登录] 登录成功 - 用户ID: {}, 手机号: {}{}",
                    user.getUserId(), req.getCountryCode(), req.getMobile());
            return ResponseVO.successResponse(buildLoginResponse(user));
        } else {
            return registerByMobile(req);
        }
    }

    /**
     * 通过手机号注册新用户
     */
    private ResponseVO registerByMobile(AuthReq req) {
        // 构造注册请求 (复用现有注册逻辑)
        RegisterReq registerReq = new RegisterReq();
        registerReq.setCountryCode(req.getCountryCode());
        registerReq.setMobile(req.getMobile());
        registerReq.setRegisterType(2); // MOBILE

        // 密码设置为空字符串 (允许空密码)
        registerReq.setPassword("");

        // 调用用户服务注册
        ResponseVO<User> registerResp = userService.registerUser(registerReq);
        if (!registerResp.isSuccess()) {
            log.error("[验证码注册] 注册失败 - 手机号: {}{}, 错误: {}",
                    req.getCountryCode(), req.getMobile(), registerResp.getMsg());
            return registerResp;
        }

        User newUser = registerResp.getData();
        log.info("[验证码注册] 注册成功 - 用户ID: {}, 手机号: {}{}",
                newUser.getUserId(), req.getCountryCode(), req.getMobile());

        return ResponseVO.successResponse(buildLoginResponse(newUser));
    }

    /**
     * 构建登录响应 (复用现有逻辑)
     */
    private LoginResp buildLoginResponse(User user) {
        LoginResp loginResp = new LoginResp();

        // ========== IM System Credentials ==========
        SigAPI sigAPI = new SigAPI(appConfig.getAppId(), appConfig.getPrivateKey());
        String imUserSig = sigAPI.genUserSig(user.getUserId(), appConfig.getImTokenExpiration());

        loginResp.setAppId(appConfig.getAppId());
        loginResp.setUserId(user.getUserId());
        loginResp.setImUserSign(imUserSig);
        loginResp.setImTokenExpireTime(System.currentTimeMillis() + appConfig.getImTokenExpiration() * 1000L);

        // ========== App Authentication ==========
        String userSign = JwtUtil.generateToken(user.getUserId());
        loginResp.setUserSign(userSign);
        loginResp.setTokenExpireTime(System.currentTimeMillis() + JwtUtil.EXPIRATION_TIME);

        // ========== User Information ==========
        loginResp.setUserName(user.getUserName());
        loginResp.setMobile(user.getMobile());
        loginResp.setCountryCode(user.getCountryCode());

        return loginResp;
    }
}
