package com.yn.anime.work.client;

import com.yn.anime.common.dto.RatingStatDTO;
import com.yn.anime.work.client.fallback.RatingClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "rating-service", fallback = RatingClientFallback.class)
public interface RatingClient {
    @GetMapping("/api/ratings/works/{workId}/stat")
    RatingStatDTO getWorkRatingStat(@PathVariable("workId") Long workId);
}
