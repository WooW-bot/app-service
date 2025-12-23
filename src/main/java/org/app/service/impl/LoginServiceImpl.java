package org.app.service.impl;

import org.app.common.ResponseVO;
import org.app.config.AppConfig;
import org.app.dao.User;
import org.app.enums.ErrorCode;
import org.app.enums.LoginTypeEnum;
import org.app.enums.RegisterTypeEnum;
import org.app.model.req.LoginReq;
import org.app.model.req.RegisterReq;
import org.app.model.resp.LoginResp;
import org.app.service.LoginService;
import org.app.service.UserService;
import org.app.utils.JwtUtil;
import org.app.utils.PasswordEncoder;
import org.app.utils.SigAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Parker
 * @date 12/21/25
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    UserService userService;

    @Autowired
    AppConfig appConfig;

    @Override
    public ResponseVO login(LoginReq req) {
        LoginResp loginResp = new LoginResp();

        if (LoginTypeEnum.USERNAME_PASSWORD.getCode() == req.getLoginType()) {
            // Username + password login
            if (req.getUserName() == null || req.getUserName().trim().isEmpty()) {
                return ResponseVO.errorResponse(ErrorCode.REGISTER_ERROR.getCode(), "用户名不能为空");
            }

            ResponseVO userByUserName = userService.getUserByUserName(req.getUserName());
            if (!userByUserName.isSuccess()) {
                return userByUserName;
            }
            User user = (User) userByUserName.getData();
            if (user == null) {
                return ResponseVO.errorResponse(ErrorCode.USER_NOT_EXIST);
            }
            // Verify password
            if (!PasswordEncoder.matches(req.getPassword(), user.getPassword())) {
                return ResponseVO.errorResponse(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
            }

            return ResponseVO.successResponse(buildLoginResponse(user));
        } else if (LoginTypeEnum.SMS_PASSWORD.getCode() == req.getLoginType()) {
            // Mobile + password login
            if (req.getMobile() == null || req.getMobile().trim().isEmpty()) {
                return ResponseVO.errorResponse(ErrorCode.REGISTER_ERROR.getCode(), "手机号不能为空");
            }
            if (req.getCountryCode() == null || req.getCountryCode().trim().isEmpty()) {
                return ResponseVO.errorResponse(ErrorCode.REGISTER_ERROR.getCode(), "国家码不能为空");
            }

            ResponseVO userByMobile = userService.getUserByMobile(req.getCountryCode(), req.getMobile());
            if (!userByMobile.isSuccess()) {
                return userByMobile;
            }
            User user = (User) userByMobile.getData();
            if (user == null) {
                return ResponseVO.errorResponse(ErrorCode.USER_NOT_EXIST);
            }
            // Verify password
            if (!PasswordEncoder.matches(req.getPassword(), user.getPassword())) {
                return ResponseVO.errorResponse(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
            }
            return ResponseVO.successResponse(buildLoginResponse(user));
        }

        return ResponseVO.errorResponse(ErrorCode.REGISTER_ERROR.getCode(), "不支持的登录方式");
    }

    /**
     * 构建登录响应对象
     *
     * @param user 用户对象
     * @return 登录响应
     */
    private LoginResp buildLoginResponse(User user) {
        LoginResp loginResp = new LoginResp();

        // 生成 IM 签名
        SigAPI sigAPI = new SigAPI(appConfig.getAppId(), appConfig.getPrivateKey());
        String imUserSig = sigAPI.genUserSig(user.getUserId(), appConfig.getImTokenExpiration());

        // 生成应用 Token (JWT)
        String userSign = JwtUtil.generateToken(user.getUserId());

        // ========== IM System Credentials ==========
        loginResp.setAppId(appConfig.getAppId());
        loginResp.setUserId(user.getUserId());
        loginResp.setImUserSign(imUserSig);
        loginResp.setImTokenExpireTime(System.currentTimeMillis() + appConfig.getImTokenExpiration() * 1000L);

        // ========== App Authentication ==========
        loginResp.setUserSign(userSign);
        loginResp.setTokenExpireTime(System.currentTimeMillis() + JwtUtil.EXPIRATION_TIME);

        // ========== User Information ==========
        loginResp.setUserName(user.getUserName());
        loginResp.setMobile(user.getMobile());
        loginResp.setCountryCode(user.getCountryCode());

        return loginResp;
    }

    @Override
    public ResponseVO register(RegisterReq req) {
        if (RegisterTypeEnum.MOBILE.getCode() == req.getRegisterType()) {
            if (req.getMobile() == null || req.getMobile().trim().isEmpty()) {
                return ResponseVO.errorResponse(ErrorCode.REGISTER_ERROR.getCode(), "手机号不能空");
            }
            if (req.getCountryCode() == null || req.getCountryCode().trim().isEmpty()) {
                return ResponseVO.errorResponse(ErrorCode.REGISTER_ERROR.getCode(), "国家码不能为空");
            }
            ResponseVO<User> userByMobile = userService.getUserByMobile(req.getCountryCode(), req.getMobile());
            if (!userByMobile.isSuccess()) {
                return userByMobile;
            }
            if (userByMobile.getData() != null) {
                return ResponseVO.errorResponse(ErrorCode.MOBILE_IS_REGISTER);
            }
            ResponseVO<User> userResponseVO = userService.registerUser(req);
            if (!userResponseVO.isSuccess()) {
                return userResponseVO;
            }
            return ResponseVO.successResponse(buildLoginResponse(userResponseVO.getData()));
        } else if (RegisterTypeEnum.USERNAME.getCode() == req.getRegisterType()) {
            if (req.getUserName() == null || req.getUserName().trim().isEmpty()) {
                return ResponseVO.errorResponse(ErrorCode.REGISTER_ERROR.getCode(), "用户名不能为空");
            }
            ResponseVO<User> userByUserName = userService.getUserByUserName(req.getUserName());
            if (!userByUserName.isSuccess()) {
                return userByUserName;
            }
            if (userByUserName.getData() != null) {
                return ResponseVO.errorResponse(ErrorCode.USERNAME_IS_REGISTER);
            }
            ResponseVO<User> userResponseVO = userService.registerUser(req);
            if (!userResponseVO.isSuccess()) {
                return userResponseVO;
            }
            return ResponseVO.successResponse(buildLoginResponse(userResponseVO.getData()));
        }
        return ResponseVO.errorResponse(ErrorCode.REGISTER_ERROR.getCode(), "不支持的注册方式");
    }
}
