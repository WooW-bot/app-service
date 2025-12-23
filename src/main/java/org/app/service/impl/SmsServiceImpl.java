package org.app.service.impl;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.app.common.ResponseVO;
import org.app.enums.ErrorCode;
import org.app.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 短信验证码服务实现 (模拟实现)
 *
 * 实现方案:
 * 1. 使用 Redis 存储验证码,Key格式: sms:{countryCode}:{mobile}
 * 2. 验证码有效期: 5分钟
 * 3. 模拟发送: 不实际调用短信服务商,仅生成并缓存验证码
 * 4. 开发环境: 返回验证码用于测试
 * 5. 生产环境: 可轻松替换为真实短信服务商
 *
 * @author Parker
 * @date 12/23/25
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    private static final String SMS_CODE_PREFIX = "sms:";
    private static final int SMS_CODE_LENGTH = 6;
    private static final int SMS_CODE_EXPIRE_MINUTES = 5;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResponseVO sendSmsCode(String countryCode, String mobile) {
        String key = buildSmsKey(countryCode, mobile);

        // 检查是否频繁发送 (60秒内不允许重复发送)
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (ttl != null && ttl > (SMS_CODE_EXPIRE_MINUTES * 60 - 60)) {
            return ResponseVO.errorResponse(ErrorCode.SMS_SEND_TOO_FREQUENT);
        }

        // 生成6位随机数字验证码
        String code = RandomUtil.randomNumbers(SMS_CODE_LENGTH);

        // 存储到 Redis, 5分钟过期
        redisTemplate.opsForValue().set(key, code, SMS_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 模拟发送短信 (实际不发送)
        log.info("[短信验证码] 发送成功 - 手机号: {}{}, 验证码: {} (有效期{}分钟)",
                countryCode, mobile, code, SMS_CODE_EXPIRE_MINUTES);

        // 开发环境返回验证码用于测试 (生产环境应去除)
        return ResponseVO.successResponse("验证码已发送 [开发环境验证码: " + code + "]");
    }

    @Override
    public boolean verifySmsCode(String countryCode, String mobile, String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }

        String key = buildSmsKey(countryCode, mobile);
        String cachedCode = redisTemplate.opsForValue().get(key);

        if (cachedCode == null) {
            log.warn("[短信验证码] 验证失败 - 验证码不存在或已过期, 手机号: {}{}", countryCode, mobile);
            return false;
        }

        boolean isValid = cachedCode.equals(code.trim());

        if (isValid) {
            // 验证成功后立即删除验证码 (一次性使用)
            redisTemplate.delete(key);
            log.info("[短信验证码] 验证成功 - 手机号: {}{}", countryCode, mobile);
        } else {
            log.warn("[短信验证码] 验证失败 - 验证码错误, 手机号: {}{}", countryCode, mobile);
        }

        return isValid;
    }

    /**
     * 构建 Redis Key
     */
    private String buildSmsKey(String countryCode, String mobile) {
        return SMS_CODE_PREFIX + countryCode + ":" + mobile;
    }
}
