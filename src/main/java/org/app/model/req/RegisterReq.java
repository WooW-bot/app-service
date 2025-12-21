package org.app.model.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Parker
 * @date 12/21/25
 */
@Data
public class RegisterReq {

    private String userName;

    @NotBlank(message = "密码不能为空")
    private String password;

    // 注册方式 1手机号注册 2用户名
    @NotNull(message = "请选择注册方式")
    private Integer registerType;

    private String proto;
}
