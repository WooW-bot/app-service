package org.app.service;

import org.app.common.ResponseVO;

/**
 * 短信验证码服务
 *
 * @author Parker
 * @date 12/23/25
 */
public interface SmsService {

    /**
     * 发送验证码
     * @param countryCode 国家码
     * @param mobile 手机号
     * @return 响应结果
     */
    ResponseVO sendSmsCode(String countryCode, String mobile);

    /**
     * 验证短信验证码
     * @param countryCode 国家码
     * @param mobile 手机号
     * @param code 验证码
     * @return 是否验证成功
     */
    boolean verifySmsCode(String countryCode, String mobile, String code);
}
