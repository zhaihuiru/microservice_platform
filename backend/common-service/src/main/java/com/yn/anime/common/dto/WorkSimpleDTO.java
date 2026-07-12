package com.yn.anime.common.dto;

import lombok.Data;

/*
    classDescription:用于收藏、评论等服务调用的返回数据
*/
@Data
public class WorkSimpleDTO {
    private Long id;

    private String title;

    private String coverUrl;
}
