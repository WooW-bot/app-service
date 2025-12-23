package org.app.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author Parker
 * @date 12/21/25
 */
@Data
@TableName("app_user")
public class User {
    @TableId(type = IdType.INPUT)
    private String userId;
    // 用户名
    private String userName;
    // 密码
    @JsonIgnore
    private String password;
    // 手机号
    private String mobile;
    // 创建时间
    private Long createTime;
    // 更新时间
    private Long updateTime;
}
