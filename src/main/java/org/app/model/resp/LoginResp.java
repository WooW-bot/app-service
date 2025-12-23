package org.app.model.resp;

import lombok.Data;

/**
 * @author Parker
 * @date 12/21/25
 */
@Data
public class LoginResp {
    // ========== IM System Credentials ==========
    /** IM系统的AppId */
    private Integer appId;

    /** 用户ID */
    private String userId;

    /** IM系统的用户签名 (用于登录IM) */
    private String imUserSign;

    /** IM Token过期时间 (毫秒时间戳) */
    private Long imTokenExpireTime;

    // ========== App Authentication ==========
    /** 应用系统的Token (用于API认证) */
    private String userSign;

    /** Token过期时间 (毫秒时间戳) */
    private Long tokenExpireTime;

    // ========== User Information ==========
    /** 用户名 */
    private String userName;

    /** 手机号 */
    private String mobile;

    /** 国家码 */
    private String countryCode;
}
