package org.app.service;

import org.app.common.ResponseVO;
import org.app.model.req.AuthReq;

/**
 * 统一认证服务
 *
 * @author Parker
 * @date 12/23/25
 */
public interface AuthService {

    /**
     * 统一认证接口 (登录或注册)
     * @param req 认证请求
     * @return 认证响应
     */
    ResponseVO auth(AuthReq req);
}
