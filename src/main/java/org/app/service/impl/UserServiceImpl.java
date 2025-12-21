package org.app.service.impl;

import org.app.common.ResponseVO;
import org.app.dao.User;
import org.app.dao.mapper.UserMapper;
import org.app.model.req.RegisterReq;
import org.app.service.ImService;
import org.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Parker
 * @date 12/21/25
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    ImService imService;

    @Override
    public ResponseVO<User> getUserByMobile(String mobile) {
        return null;
    }

    @Override
    public ResponseVO<User> registerUser(RegisterReq req) {
        return null;
    }
}
