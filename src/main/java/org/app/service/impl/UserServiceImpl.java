package org.app.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.common.ResponseVO;
import org.app.dao.User;
import org.app.dao.mapper.UserMapper;
import org.app.enums.ErrorCode;
import org.app.enums.RegisterTypeEnum;
import org.app.exception.ApplicationException;
import org.app.model.req.RegisterReq;
import org.app.model.resp.ImportUserResp;
import org.app.service.ImService;
import org.app.service.UserService;
import org.app.utils.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author Parker
 * @date 12/21/25
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    ImService imService;

    @Override
    public ResponseVO getUserByMobile(String mobile) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getMobile, mobile);
        User user = userMapper.selectOne(wrapper);
        return ResponseVO.successResponse(user);
    }

    @Override
    @Transactional
    public ResponseVO<User> registerUser(RegisterReq req) {
        User user = new User();
        user.setCreateTime(System.currentTimeMillis());

        // 手动生成userId（使用雪花算法，避免数据库插入后IM导入失败的问题）
        user.setUserId(String.valueOf(System.currentTimeMillis()) + RandomUtil.randomNumbers(6));

        // 加密密码
        String encodedPassword = PasswordEncoder.encode(req.getPassword());
        user.setPassword(encodedPassword);

        if (RegisterTypeEnum.MOBILE.getCode() == req.getRegisterType()) {
            user.setMobile(req.getMobile());
            user.setUserName("user_" + RandomUtil.randomNumbers(8));
        } else if (RegisterTypeEnum.USERNAME.getCode() == req.getRegisterType()) {
            user.setUserName(req.getUserName());
        }

        ArrayList<User> users = new ArrayList<>();
        users.add(user);
        ResponseVO responseVO = imService.importUser(users);
        if (responseVO.isSuccess()) {
            Object data = responseVO.getData();
            ObjectMapper objectMapper = new ObjectMapper();
            ImportUserResp importUserResp = objectMapper.convertValue(data, ImportUserResp.class);
            Set<String> successId = importUserResp.getSuccessId();
            if (successId.contains(user.getUserId())) {
                // IM导入成功后，再插入本地数据库
                userMapper.insert(user);
                return ResponseVO.successResponse(user);
            } else {
                throw new ApplicationException(ErrorCode.REGISTER_ERROR.getCode(), "IM系统导入用户失败");
            }
        } else {
            throw new ApplicationException(responseVO.getCode(), responseVO.getMsg());
        }
    }
}
