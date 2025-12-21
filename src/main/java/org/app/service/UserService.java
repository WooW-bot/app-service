package org.app.service;

import org.app.common.ResponseVO;
import org.app.dao.User;
import org.app.model.req.RegisterReq;

/**
 * @author Parker
 * @date 12/21/25
 */
public interface UserService {
    ResponseVO<User> getUserByMobile(String mobile);
    ResponseVO<User> registerUser(RegisterReq req);
}
