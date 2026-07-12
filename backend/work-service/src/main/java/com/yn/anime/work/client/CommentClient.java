package com.yn.anime.work.client;

import com.yn.anime.common.dto.CommentDTO;
import com.yn.anime.work.client.fallback.CommentClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "comment-service", fallback = CommentClientFallback.class)
public interface CommentClient {
    @GetMapping("/api/comments")
    List<CommentDTO> getComments(@RequestParam("targetType") String targetType,
                                 @RequestParam("targetId") Long targetId);
}
