package com.yn.anime.auth.config;

import com.yn.anime.auth.entity.AuthRole;
import com.yn.anime.auth.entity.AuthUser;
import com.yn.anime.auth.entity.AuthUserRole;
import com.yn.anime.auth.repository.AuthRoleRepository;
import com.yn.anime.auth.repository.AuthUserRepository;
import com.yn.anime.auth.repository.AuthUserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {
    private final AuthUserRepository userRepository;
    private final AuthRoleRepository roleRepository;
    private final AuthUserRoleRepository userRoleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(AuthUserRepository userRepository,
                           AuthRoleRepository roleRepository,
                           AuthUserRoleRepository userRoleRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        AuthRole adminRole = createRoleIfAbsent("ADMIN", "管理员", "系统预置管理员，拥有用户管理权限");
        AuthRole userRole = createRoleIfAbsent("USER", "普通用户", "平台普通注册用户");

        AuthUser admin = userRepository.findByUsername("admin").orElseGet(() -> {
            AuthUser u = new AuthUser();
            u.setUsername("admin");
            u.setEmail("admin@anime.local");
            u.setNickname("系统管理员");
            u.setPasswordHash(passwordEncoder.encode("admin123456"));
            u.setStatus(0);
            return userRepository.save(u);
        });

        if (!userRoleRepository.existsByUserIdAndRoleId(admin.getId(), adminRole.getId())) {
            AuthUserRole relation = new AuthUserRole();
            relation.setUserId(admin.getId());
            relation.setRoleId(adminRole.getId());
            userRoleRepository.save(relation);
        }

        // 防止管理员没有 USER 基础角色。也可以不加，看你们项目定义。
        if (!userRoleRepository.existsByUserIdAndRoleId(admin.getId(), userRole.getId())) {
            AuthUserRole relation = new AuthUserRole();
            relation.setUserId(admin.getId());
            relation.setRoleId(userRole.getId());
            userRoleRepository.save(relation);
        }
    }

    private AuthRole createRoleIfAbsent(String roleCode, String roleName, String description) {
        return roleRepository.findByRoleCode(roleCode).orElseGet(() -> {
            AuthRole role = new AuthRole();
            role.setRoleCode(roleCode);
            role.setRoleName(roleName);
            role.setDescription(description);
            return roleRepository.save(role);
        });
    }
}