package org.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Parker
 * @date 12/21/25
 */
@Data
@Component
@ConfigurationProperties(prefix = "appconfig")
public class AppConfig {

    private String imUrl;

    private String imVersion;

    private Integer appId;

    private String adminId;

    private String privateKey;

    private String jwtKey;

    private Integer jwtExpireTime;

    /** IM Token有效期 (秒) */
    private Integer imTokenExpiration = 500000; // 默认约5.8天

}
