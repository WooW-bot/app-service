package org.app.utils;

/**
 * 国家码标准化工具类
 * 处理各种国家码输入格式，统一转换为 E.164 标准格式 (+xxx)
 * 
 * @author Parker
 * @date 12/23/25
 */
public class CountryCodeUtil {

    /**
     * 标准化国家码
     * 支持的输入格式:
     * - +86 → +86 (已标准化)
     * - 86 → +86 (添加+)
     * - 0086 → +86 (00前缀转换为+)
     * - 00 86 → +86 (去除空格后转换)
     * 
     * @param countryCode 原始国家码
     * @return 标准化后的国家码 (E.164格式: +xxx)
     * @throws IllegalArgumentException 如果国家码格式无效
     */
    public static String normalize(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            throw new IllegalArgumentException("国家码不能为空");
        }

        // 去除所有空格
        String normalized = countryCode.trim().replaceAll("\\s+", "");

        // 情况1: 已经有 + 前缀，直接返回
        if (normalized.startsWith("+")) {
            return validateAndReturn(normalized);
        }

        // 情况2: 00 前缀（国际拨号前缀），转换为 +
        if (normalized.startsWith("00")) {
            normalized = "+" + normalized.substring(2);
            return validateAndReturn(normalized);
        }

        // 情况3: 纯数字，添加 + 前缀
        if (normalized.matches("^\\d+$")) {
            normalized = "+" + normalized;
            return validateAndReturn(normalized);
        }

        // 其他情况：无效格式
        throw new IllegalArgumentException("国家码格式无效: " + countryCode);
    }

    /**
     * 验证并返回标准化的国家码
     * 
     * @param countryCode 待验证的国家码
     * @return 验证通过的国家码
     * @throws IllegalArgumentException 如果格式无效
     */
    private static String validateAndReturn(String countryCode) {
        // 验证格式: + 后面跟 1-4 位数字
        if (!countryCode.matches("^\\+\\d{1,4}$")) {
            throw new IllegalArgumentException("国家码格式无效: " + countryCode + " (应为 +1 到 +9999)");
        }
        return countryCode;
    }

    /**
     * 检查国家码是否有效
     * 
     * @param countryCode 国家码
     * @return true 如果有效，false 如果无效
     */
    public static boolean isValid(String countryCode) {
        try {
            normalize(countryCode);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 获取常用国家码列表（用于前端下拉选择）
     * 
     * @return 常用国家码数组
     */
    public static String[] getCommonCountryCodes() {
        return new String[] {
                "+86", // 中国
                "+1", // 美国/加拿大
                "+44", // 英国
                "+81", // 日本
                "+82", // 韩国
                "+84", // 越南
                "+65", // 新加坡
                "+60", // 马来西亚
                "+66", // 泰国
                "+91", // 印度
                "+61", // 澳大利亚
                "+33", // 法国
                "+49", // 德国
                "+7", // 俄罗斯
                "+39", // 意大利
                "+34", // 西班牙
        };
    }
}
