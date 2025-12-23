package org.app.model.req;

import lombok.Data;
import org.app.utils.CountryCodeUtil;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Parker
 * @date 12/21/25
 */
@Data
public class LoginReq {

    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String userName;

    // 国家码 (默认: +86 中国)
    @Pattern(regexp = "^\\+\\d{1,4}$", message = "国家码格式不正确")
    private String countryCode = "+86";

    // 手机号 (6-15位数字，支持国际格式)
    @Pattern(regexp = "^\\d{6,15}$", message = "手机号必须是6-15位数字")
    private String mobile;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;

    @NotNull(message = "请选择登录方式")
    // 登录方式 1用户名+密码 3手机号+密码
    private Integer loginType;

    /**
     * 设置国家码时自动标准化
     * 支持: +86, 86, 0086 等格式
     */
    public void setCountryCode(String countryCode) {
        if (countryCode != null && !countryCode.trim().isEmpty()) {
            this.countryCode = CountryCodeUtil.normalize(countryCode);
        } else {
            this.countryCode = "+86"; // 默认中国
        }
    }
}
