package org.app.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token 工具类
 * 用于生成和验证应用系统的认证 Token
 * 
 * @author Parker
 * @date 12/23/25
 */
public class JwtUtil {

    // Token 有效期: 7天 (毫秒)
    public static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L;

    // 密钥 (生产环境应从配置文件读取)
    private static final String SECRET_KEY = "your-256-bit-secret-key-for-jwt-token-generation-please-change-in-production";

    // 使用 HMAC-SHA256 算法的密钥
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * 生成 JWT Token
     * 
     * @param userId 用户ID
     * @return JWT Token 字符串
     */
    public static String generateToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从 Token 中提取用户ID
     * 
     * @param token JWT Token
     * @return 用户ID
     */
    public static String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * 验证 Token 是否有效
     * 
     * @param token JWT Token
     * @return true 如果有效，false 如果无效或过期
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return !expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从 Token 中获取 Claims
     * 
     * @param token JWT Token
     * @return Claims 对象
     */
    private static Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取 Token 过期时间
     * 
     * @param token JWT Token
     * @return 过期时间 (毫秒时间戳)
     */
    public static Long getExpirationTime(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration().getTime();
    }

    /**
     * 检查 Token 是否即将过期 (1天内)
     * 
     * @param token JWT Token
     * @return true 如果即将过期
     */
    public static boolean isTokenExpiringSoon(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            long timeUntilExpiration = expiration.getTime() - System.currentTimeMillis();
            return timeUntilExpiration < 24 * 60 * 60 * 1000; // 小于1天
        } catch (Exception e) {
            return true;
        }
    }
}
