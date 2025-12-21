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
            ResponseVO<User> userByUserName = userService.getUserByMobile(req.getUserName());
            if (userByUserName.isSuccess()) {
                return ResponseVO.errorResponse(ErrorCode.REGISTER_ERROR);
            }
            ResponseVO<User> userResponseVO = userService.registerUser(req);
            return userResponseVO;
        }
        return ResponseVO.successResponse();
    }
}
