package com.yn.anime.work.client.fallback;

import com.yn.anime.common.dto.FavoriteCheckDTO;
import com.yn.anime.work.client.FavoriteClient;
import org.springframework.stereotype.Component;

@Component
public class FavoriteClientFallback implements FavoriteClient {
    @Override
    public FavoriteCheckDTO checkFavorite(Long userId, String targetType, Long targetId) {
        return new FavoriteCheckDTO(userId, targetType, targetId, false);
    }
}
