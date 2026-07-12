package com.yn.anime.work.client.fallback;

import com.yn.anime.common.dto.CommentDTO;
import com.yn.anime.work.client.CommentClient;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CommentClientFallback implements CommentClient {
    @Override
    public List<CommentDTO> getComments(String targetType, Long targetId) {
        return List.of();
    }
}
