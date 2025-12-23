package org.app.model.req;

import lombok.Data;
import org.app.utils.CountryCodeUtil;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 发送短信验证码请求
 *
 * @author Parker
 * @date 12/23/25
 */
@Data
public class SendSmsReq {

    @NotBlank(message = "国家码不能为空")
    @Pattern(regexp = "^\\+\\d{1,4}$", message = "国家码格式不正确")
    private String countryCode = "+86";

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^\\d{6,15}$", message = "手机号必须是6-15位数字")
    private String mobile;

    public void setCountryCode(String countryCode) {
        if (countryCode != null && !countryCode.trim().isEmpty()) {
            this.countryCode = CountryCodeUtil.normalize(countryCode);
        } else {
            this.countryCode = "+86";
        }
    }
}
