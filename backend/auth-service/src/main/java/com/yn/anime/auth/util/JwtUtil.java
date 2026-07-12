package com.yn.anime.auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtUtil {
    @Value("${anime.jwt.secret:anime-platform-secret-key-change-me}")
    private String secret;

    @Value("${anime.jwt.expire-millis:86400000}")
    private long expireMillis;

    public String generateToken(Long userId, String username, List<String> roles) {
        long exp = System.currentTimeMillis() + expireMillis;
        String rolesText = String.join(",", roles);

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = "{"
                + "\"userId\":" + userId + ","
                + "\"username\":\"" + escape(username) + "\","
                + "\"roles\":\"" + escape(rolesText) + "\","
                + "\"exp\":" + exp
                + "}";

        String header = base64Url(headerJson);
        String payload = base64Url(payloadJson);
        String sign = sign(header + "." + payload);

        return header + "." + payload + "." + sign;
    }

    public TokenInfo parseToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token 为空");
        }

        String pureToken = token.replace("Bearer ", "").trim();
        String[] parts = pureToken.split("\\.");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Token 格式错误");
        }

        String data = parts[0] + "." + parts[1];
        String expectedSign = sign(data);

        if (!expectedSign.equals(parts[2])) {
            throw new IllegalArgumentException("Token 签名无效");
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

        Long userId = getLong(payloadJson, "userId");
        String username = getString(payloadJson, "username");
        String rolesText = getString(payloadJson, "roles");
        Long exp = getLong(payloadJson, "exp");

        if (System.currentTimeMillis() > exp) {
            throw new IllegalArgumentException("Token 已过期");
        }

        List<String> roles = rolesText == null || rolesText.isBlank()
                ? List.of()
                : Arrays.asList(rolesText.split(","));

        return new TokenInfo(userId, username, roles, exp);
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(key);
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        } catch (Exception e) {
            throw new IllegalStateException("Token 签名失败", e);
        }
    }

    private String base64Url(String text) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private Long getLong(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Token 缺少字段：" + field);
        }
        return Long.parseLong(matcher.group(1));
    }

    private String getString(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) {
            return "";
        }
        return matcher.group(1);
    }

    public record TokenInfo(
            Long userId,
            String username,
            List<String> roles,
            Long exp
    ) {}
}