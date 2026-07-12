package com.yn.anime.person.vo;

import lombok.Data;

/*
    classDescription:
*/
@Data
public class PersonWorkVO {
    private Long workId;

    private String workTitle;

    private String roleType;

    /**
     * 来源
     *
     * 幕后人员
     * 声优演员
     */
    private String source;

    /**
     * 角色id
     * 声优时存在
     */
    private Long characterId;

}