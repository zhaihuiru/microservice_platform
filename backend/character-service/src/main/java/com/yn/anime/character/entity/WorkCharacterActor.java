package com.yn.anime.character.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
@TableName("work_character_actor")
public class WorkCharacterActor {

    /** 逻辑关联 work_db.works.id，不跨服务引用实体 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long characterId;

    /** 逻辑关联 person_db.persons.id，不跨服务引用实体 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long personId;

    private String roleType;
}
