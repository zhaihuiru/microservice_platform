package com.yn.anime.common.dto;

import java.util.List;

public record AssignRoleRequest(
        List<String> roles
) {}