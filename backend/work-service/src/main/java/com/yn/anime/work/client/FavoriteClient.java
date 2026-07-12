package com.yn.anime.work.client;

import com.yn.anime.common.dto.FavoriteCheckDTO;
import com.yn.anime.work.client.fallback.FavoriteClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "favorite-service", fallback = FavoriteClientFallback.class)
public interface FavoriteClient {
    @GetMapping("/api/favorites/check")
    FavoriteCheckDTO checkFavorite(@RequestParam("userId") Long userId,
                                   @RequestParam("targetType") String targetType,
                                   @RequestParam("targetId") Long targetId);
}
