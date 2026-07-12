package com.yn.anime.gateway.filter;

import com.yn.anime.gateway.util.JwtTokenUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private final JwtTokenUtil jwtTokenUtil;

    public AuthGlobalFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();

        // 公开接口直接放行
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authorization == null || authorization.isBlank()) {
            String tokenParam = exchange.getRequest().getQueryParams().getFirst("token");
            if (tokenParam != null && !tokenParam.isBlank()) {
                authorization = "Bearer " + tokenParam;
            }
        }

        if (authorization == null || authorization.isBlank()) {
            return writeJson(exchange, HttpStatus.UNAUTHORIZED, 401, "未登录或 Token 缺失");
        }

        JwtTokenUtil.TokenInfo tokenInfo;

        try {
            tokenInfo = jwtTokenUtil.parseToken(authorization);
        } catch (Exception e) {
            return writeJson(exchange, HttpStatus.UNAUTHORIZED, 401, e.getMessage());
        }

        // 管理员接口必须 ADMIN 角色
        if (isAdminPath(path) && !tokenInfo.roles().contains("ADMIN")) {
            return writeJson(exchange, HttpStatus.FORBIDDEN, 403, "权限不足，只有管理员可以访问该接口");
        }

        // 清理客户端伪造的身份头，再写入 Gateway 解析出的真实身份
        ServerHttpRequest request = exchange.getRequest().mutate()
//                .headers(headers -> {
//                    headers.remove("X-User-Id");
//                    headers.remove("X-Username");
//                    headers.remove("X-User-Roles");
//
//                    headers.set("X-User-Id", String.valueOf(tokenInfo.userId()));
//                    headers.set("X-Username", tokenInfo.username());
//                    headers.set("X-User-Roles", String.join(",", tokenInfo.roles()));
//                })
                .headers(headers -> {
                    // 删除客户端可能伪造的身份请求头
                    headers.remove("X-User-Id");
                    headers.remove("X-Username");
                    headers.remove("X-User-Roles");
                    headers.remove("User-Id");
                    headers.remove("User-Role");

                    String userId = String.valueOf(tokenInfo.userId());
                    String roles = String.join(",", tokenInfo.roles());
                    String primaryRole = tokenInfo.roles().contains("ADMIN")
                            ? "ADMIN"
                            : "USER";

                    // 标准请求头
                    headers.set("X-User-Id", userId);
                    headers.set("X-Username", tokenInfo.username());
                    headers.set("X-User-Roles", roles);

                    // 兼容评分、评论、收藏服务
                    headers.set("User-Id", userId);
                    headers.set("User-Role", primaryRole);
                })
                .build();

        return chain.filter(exchange.mutate().request(request).build());
    }

    /**
     * 公开接口：
     * 1. 登录、注册
     * 2. 各服务 ping
     * 3. 作品、角色、人物浏览
     * 4. actuator 健康检查
     */
    private boolean isPublicPath(String path) {
        if (path == null) {
            return false;
        }

        if (path.equals("/api/auth/login")
                || path.equals("/api/auth/register")
                || path.equals("/api/auth/ping")) {
            return true;
        }

        // 为了方便开发测试，各服务 ping 放行
        if (path.endsWith("/ping")) {
            return true;
        }

        // 公开浏览类接口
        if (path.equals("/api/works") || path.startsWith("/api/works/")) {
            return true;
        }

        if (path.equals("/api/characters") || path.startsWith("/api/characters/")) {
            return true;
        }

        if (path.equals("/api/persons") || path.startsWith("/api/persons/")) {
            return true;
        }

        // Gateway 自身健康检查
        if (path.startsWith("/actuator/")) {
            return true;
        }

        return false;
    }

    private boolean isAdminPath(String path) {
        return path != null && (
                path.startsWith("/api/admin/")
                        || path.startsWith("/api/notifications/admin/")
                        || path.startsWith("/api/chats/admin/")
        );
    }

    private Mono<Void> writeJson(ServerWebExchange exchange,
                                 HttpStatus status,
                                 int code,
                                 String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-Id");

        String body = "{"
                + "\"code\":" + code + ","
                + "\"message\":\"" + escape(message) + "\","
                + "\"data\":null,"
                + "\"requestId\":\"" + escape(requestId == null ? "" : requestId) + "\""
                + "}";

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    public int getOrder() {
        // RequestTraceFilter 是 -100，这里设为 -90，保证先生成 X-Request-Id，再鉴权
        return -90;
    }
}