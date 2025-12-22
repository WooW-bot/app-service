package org.app.service;

import org.app.common.ResponseVO;
import org.app.dao.User;
import org.app.model.req.RegisterReq;

/**
 * @author Parker
 * @date 12/21/25
 */
public interface UserService {
    ResponseVO getUserByMobile(String mobile);
    ResponseVO registerUser(RegisterReq req);
}
