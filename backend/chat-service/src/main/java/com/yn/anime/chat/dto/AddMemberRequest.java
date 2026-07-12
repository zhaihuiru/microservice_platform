package com.yn.anime.chat.dto;

import java.util.List;

public record AddMemberRequest(
        List<Long> userIds
) {
}