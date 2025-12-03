package com.campus.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 */
@Component
public class JwtUtils {

    // 密钥，实际生产中应配置在 application.yml 中
    // 使用固定的密钥，防止重启后 Token 失效
    private static final String SECRET = "CampusSecondHandMarketProjectSecretKeyForJwtTokenGeneration2023";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 过期时间 24小时
    private static final long EXPIRATION = 86400000L;

    /**
     * 生成 Token
     */
    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY)
                .compact();
    }

    /**
     * 解析 Token 获取 Claims
     */
    public Claims getClaimsByToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            getClaimsByToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
