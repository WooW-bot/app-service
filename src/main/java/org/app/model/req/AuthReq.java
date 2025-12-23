package org.app.model.req;

import lombok.Data;
import org.app.utils.CountryCodeUtil;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 统一认证请求 (登录/注册)
 * 支持两种认证方式:
 * 1. 手机号+密码 (仅登录)
 * 2. 手机号+验证码 (登录或注册)
 *
 * @author Parker
 * @date 12/23/25
 */
@Data
public class AuthReq {

    /**
     * 国家码 (默认: +86 中国)
     */
    @NotBlank(message = "国家码不能为空")
    @Pattern(regexp = "^\\+\\d{1,4}$", message = "国家码格式不正确")
    private String countryCode = "+86";

    /**
     * 手机号 (必填)
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^\\d{6,15}$", message = "手机号必须是6-15位数字")
    private String mobile;

    /**
     * 密码 (密码登录时必填, 6-20位)
     * 与 smsCode 二选一
     */
    @Pattern(regexp = "^.{6,20}$", message = "密码长度必须在6-20个字符之间")
    private String password;

    /**
     * 短信验证码 (验证码登录时必填, 6位数字)
     * 与 password 二选一
     */
    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是6位数字")
    private String smsCode;

    /**
     * 设置国家码时自动标准化
     */
    public void setCountryCode(String countryCode) {
        if (countryCode != null && !countryCode.trim().isEmpty()) {
            this.countryCode = CountryCodeUtil.normalize(countryCode);
        } else {
            this.countryCode = "+86";
        }
    }

    /**
     * 判断是否为密码认证方式
     */
    public boolean isPasswordAuth() {
        return password != null && !password.trim().isEmpty();
    }

    /**
     * 判断是否为验证码认证方式
     */
    public boolean isSmsCodeAuth() {
        return smsCode != null && !smsCode.trim().isEmpty();
    }
}
