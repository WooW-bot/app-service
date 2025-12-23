package org.app.service.impl;

import org.app.common.ResponseVO;
import org.app.dao.User;
import org.app.enums.ErrorCode;
import org.app.enums.RegisterTypeEnum;
import org.app.model.req.LoginReq;
import org.app.model.req.RegisterReq;
import org.app.service.LoginService;
import org.app.service.UserService;
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

    @Override
    public ResponseVO login(LoginReq req) {
        return null;
    }

    @Override
    public ResponseVO register(RegisterReq req) {
        if (RegisterTypeEnum.MOBILE.getCode() == req.getRegisterType()) {
            if (req.getMobile() == null || req.getMobile().trim().isEmpty()) {
                return ResponseVO.errorResponse(ErrorCode.REGISTER_ERROR.getCode(), "手机号不能空");
            }
            ResponseVO<User> userByMobile = userService.getUserByMobile(req.getMobile());
            if (!userByMobile.isSuccess()) {
                return userByMobile;
            }
            if (userByMobile.getData() != null) {
                return ResponseVO.errorResponse(ErrorCode.MOBILE_IS_REGISTER);
            }
            ResponseVO<User> userResponseVO = userService.registerUser(req);
            return userResponseVO;
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
            return userResponseVO;
        }
        return ResponseVO.errorResponse(ErrorCode.REGISTER_ERROR.getCode(), "不支持的注册方式");
    }
}
