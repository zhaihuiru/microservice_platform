package com.yn.anime.work.client.fallback;

import com.yn.anime.common.dto.RatingStatDTO;
import com.yn.anime.work.client.RatingClient;
import org.springframework.stereotype.Component;

@Component
public class RatingClientFallback implements RatingClient {
    @Override
    public RatingStatDTO getWorkRatingStat(Long workId) {
        return new RatingStatDTO(workId, 0.0, 0L, 0.0);
    }
}
