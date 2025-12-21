package org.app.service;

import org.app.common.ResponseVO;
import org.app.model.req.LoginReq;
import org.app.model.req.RegisterReq;

/**
 * @author Parker
 * @date 12/21/25
 */
public interface LoginService {
    ResponseVO login(LoginReq req);
    ResponseVO register(RegisterReq req);
}
