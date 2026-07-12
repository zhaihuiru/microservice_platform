package com.yn.anime.auth.service;

import com.yn.anime.auth.entity.AuthRole;
import com.yn.anime.auth.entity.AuthUser;
import com.yn.anime.auth.entity.AuthUserRole;
import com.yn.anime.auth.entity.UserOperationLog;
import com.yn.anime.auth.repository.AuthRoleRepository;
import com.yn.anime.auth.repository.AuthUserRepository;
import com.yn.anime.auth.repository.AuthUserRoleRepository;
import com.yn.anime.auth.repository.UserOperationLogRepository;
import com.yn.anime.auth.util.JwtUtil;
import com.yn.anime.common.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthUserService {
    private final AuthUserRepository userRepository;
    private final AuthRoleRepository roleRepository;
    private final AuthUserRoleRepository userRoleRepository;
    private final UserOperationLogRepository operationLogRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthUserService(AuthUserRepository userRepository,
                           AuthRoleRepository roleRepository,
                           AuthUserRoleRepository userRoleRepository,
                           UserOperationLogRepository operationLogRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.operationLogRepository = operationLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        validateRegisterRequest(request);

        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("用户名已存在");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("邮箱已被注册");
        }

        AuthRole userRole = roleRepository.findByRoleCode("USER")
                .orElseThrow(() -> new IllegalStateException("系统角色 USER 未初始化"));

        AuthUser user = new AuthUser();
        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim());
        user.setNickname(isBlank(request.nickname()) ? request.username().trim() : request.nickname().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setAvatarUrl("https://example.com/default-avatar.png");
        user.setStatus(0);

        AuthUser saved = userRepository.save(user);

        AuthUserRole relation = new AuthUserRole();
        relation.setUserId(saved.getId());
        relation.setRoleId(userRole.getId());
        userRoleRepository.save(relation);

        return new RegisterResponse(saved.getId(), saved.getUsername(), saved.getNickname(), "USER");
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        if (request == null || isBlank(request.username()) || isBlank(request.password())) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }

        AuthUser user = userRepository.findByUsername(request.username().trim())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new IllegalArgumentException("账号已被封禁，无法登录");
        }

        if (user.getStatus() != null && user.getStatus() == 2) {
            throw new IllegalArgumentException("账号已注销，无法登录");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("密码错误");
        }

        List<String> roles = getRoleCodesByUserId(user.getId());

        user.setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roles);

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatarUrl(),
                token,
                roles
        );
    }

    public TokenCheckResponse checkToken(String authorization) {
        JwtUtil.TokenInfo info = parseAuthorization(authorization);
        return new TokenCheckResponse(info.userId(), info.username(), info.roles(), true);
    }

    public UserProfileDTO getMe(String authorization) {
        JwtUtil.TokenInfo info = parseAuthorization(authorization);
        AuthUser user = getUserEntity(info.userId());
        return toUserProfileDTO(user);
    }

    @Transactional
    public UserProfileDTO updateMe(String authorization, UpdateProfileRequest request) {
        JwtUtil.TokenInfo info = parseAuthorization(authorization);
        AuthUser user = getUserEntity(info.userId());

        if (request.email() != null && !request.email().isBlank()) {
            userRepository.findByEmail(request.email().trim()).ifPresent(existing -> {
                if (!existing.getId().equals(user.getId())) {
                    throw new IllegalArgumentException("邮箱已被其他用户使用");
                }
            });
            user.setEmail(request.email().trim());
        }

        if (request.nickname() != null) {
            user.setNickname(request.nickname().trim());
        }

        if (request.avatarUrl() != null) {
            user.setAvatarUrl(request.avatarUrl().trim());
        }

        if (request.bio() != null) {
            user.setBio(request.bio().trim());
        }

        AuthUser saved = userRepository.save(user);
        return toUserProfileDTO(saved);
    }

    @Transactional
    public void changePassword(String authorization, ChangePasswordRequest request) {
        JwtUtil.TokenInfo info = parseAuthorization(authorization);
        AuthUser user = getUserEntity(info.userId());

        if (request == null || isBlank(request.oldPassword()) || isBlank(request.newPassword())) {
            throw new IllegalArgumentException("原密码和新密码不能为空");
        }

        if (request.newPassword().length() < 6) {
            throw new IllegalArgumentException("新密码长度不能少于 6 位");
        }

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("原密码错误");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public UserDTO getUserById(Long id) {
        AuthUser user = getUserEntity(id);
        return new UserDTO(user.getId(), user.getUsername(), user.getNickname(), user.getAvatarUrl(), user.getStatus());
    }

    public UserStatusDTO getUserStatus(Long id) {
        AuthUser user = getUserEntity(id);
        return new UserStatusDTO(user.getId(), user.getStatus(), statusMessage(user.getStatus()));
    }

    public List<UserDTO> batchUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        return userRepository.findAllById(ids).stream()
                .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getNickname(), user.getAvatarUrl(), user.getStatus()))
                .toList();
    }

    public Page<AdminUserDTO> listUsers(String authorization, int page, int size) {
        requireAdmin(authorization);
        return userRepository.findAll(PageRequest.of(page, size)).map(this::toAdminUserDTO);
    }

    public AdminUserDTO getAdminUserDetail(String authorization, Long userId) {
        requireAdmin(authorization);
        AuthUser user = getUserEntity(userId);
        return toAdminUserDTO(user);
    }

    @Transactional
    public void banUser(String authorization, Long targetUserId, String reason, String requestId) {
        JwtUtil.TokenInfo operator = requireAdmin(authorization);
        AuthUser target = getUserEntity(targetUserId);

        if (operator.userId().equals(targetUserId)) {
            throw new IllegalArgumentException("管理员不能封禁自己");
        }

        if (hasRole(targetUserId, "ADMIN")) {
            throw new IllegalArgumentException("不能封禁管理员账号");
        }

        target.setStatus(1);
        userRepository.save(target);

        saveOperationLog(operator.userId(), targetUserId, "BAN_USER",
                "封禁用户，原因：" + safeReason(reason), requestId);
    }

    @Transactional
    public void unbanUser(String authorization, Long targetUserId, String requestId) {
        JwtUtil.TokenInfo operator = requireAdmin(authorization);
        AuthUser target = getUserEntity(targetUserId);

        target.setStatus(0);
        userRepository.save(target);

        saveOperationLog(operator.userId(), targetUserId, "UNBAN_USER", "解封用户", requestId);
    }

    @Transactional
    public void resetPassword(String authorization, Long targetUserId, String newPassword, String requestId) {
        JwtUtil.TokenInfo operator = requireAdmin(authorization);

        if (isBlank(newPassword) || newPassword.length() < 6) {
            throw new IllegalArgumentException("新密码长度不能少于 6 位");
        }

        AuthUser target = getUserEntity(targetUserId);
        target.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(target);

        saveOperationLog(operator.userId(), targetUserId, "RESET_PASSWORD", "重置用户密码", requestId);
    }

    @Transactional
    public void assignRoles(String authorization, Long targetUserId, AssignRoleRequest request, String requestId) {
        JwtUtil.TokenInfo operator = requireAdmin(authorization);
        AuthUser target = getUserEntity(targetUserId);

        if (request == null || request.roles() == null || request.roles().isEmpty()) {
            throw new IllegalArgumentException("角色列表不能为空");
        }

        List<AuthRole> roles = request.roles().stream()
                .map(roleCode -> roleRepository.findByRoleCode(roleCode)
                        .orElseThrow(() -> new IllegalArgumentException("角色不存在：" + roleCode)))
                .toList();

        userRoleRepository.deleteByUserId(target.getId());

        for (AuthRole role : roles) {
            AuthUserRole relation = new AuthUserRole();
            relation.setUserId(target.getId());
            relation.setRoleId(role.getId());
            userRoleRepository.save(relation);
        }

        saveOperationLog(operator.userId(), targetUserId, "ASSIGN_ROLE",
                "分配角色：" + String.join(",", request.roles()), requestId);
    }

    public List<UserOperationLog> getUserLogs(String authorization, Long targetUserId) {
        requireAdmin(authorization);
        return operationLogRepository.findByTargetUserIdOrderByCreatedAtDesc(targetUserId);
    }

    private AuthUser getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在：" + id));
    }

    private List<String> getRoleCodesByUserId(Long userId) {
        List<Long> roleIds = userRoleRepository.findByUserId(userId).stream()
                .map(AuthUserRole::getRoleId)
                .toList();

        if (roleIds.isEmpty()) {
            return List.of();
        }

        return roleRepository.findAllById(roleIds).stream()
                .map(AuthRole::getRoleCode)
                .toList();
    }

    private boolean hasRole(Long userId, String roleCode) {
        return getRoleCodesByUserId(userId).contains(roleCode);
    }

    private JwtUtil.TokenInfo requireAdmin(String authorization) {
        JwtUtil.TokenInfo info = parseAuthorization(authorization);
        if (!info.roles().contains("ADMIN")) {
            throw new IllegalArgumentException("无管理员权限");
        }
        return info;
    }

    private JwtUtil.TokenInfo parseAuthorization(String authorization) {
        if (isBlank(authorization)) {
            throw new IllegalArgumentException("缺少 Authorization 请求头");
        }
        return jwtUtil.parseToken(authorization);
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("注册请求不能为空");
        }

        if (isBlank(request.username())) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        if (isBlank(request.email())) {
            throw new IllegalArgumentException("邮箱不能为空");
        }

        if (isBlank(request.password())) {
            throw new IllegalArgumentException("密码不能为空");
        }

        if (request.username().trim().length() < 3) {
            throw new IllegalArgumentException("用户名长度不能少于 3 位");
        }

        if (request.password().length() < 6) {
            throw new IllegalArgumentException("密码长度不能少于 6 位");
        }

        if (!request.email().contains("@")) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }
    }

    private UserProfileDTO toUserProfileDTO(AuthUser user) {
        return new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getStatus(),
                getRoleCodesByUserId(user.getId()),
                user.getLastLoginAt(),
                user.getCreatedAt()
        );
    }

    private AdminUserDTO toAdminUserDTO(AuthUser user) {
        return new AdminUserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getStatus(),
                getRoleCodesByUserId(user.getId()),
                user.getLastLoginAt(),
                user.getCreatedAt()
        );
    }

    private void saveOperationLog(Long operatorId, Long targetUserId, String type, String description, String requestId) {
        UserOperationLog log = new UserOperationLog();
        log.setOperatorId(operatorId);
        log.setTargetUserId(targetUserId);
        log.setOperationType(type);
        log.setDescription(description);
        log.setRequestId(requestId);
        operationLogRepository.save(log);
    }

    private String statusMessage(Integer status) {
        if (status == null) return "unknown";
        return switch (status) {
            case 0 -> "normal";
            case 1 -> "banned";
            case 2 -> "deleted";
            default -> "unknown";
        };
    }

    private String safeReason(String reason) {
        return isBlank(reason) ? "未填写原因" : reason.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public List<Long> listActiveUserIds() {
        return userRepository.findActiveUserIds();
    }
}