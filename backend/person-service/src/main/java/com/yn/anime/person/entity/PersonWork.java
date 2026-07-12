package com.yn.anime.person.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Data
@TableName("person_work")
public class PersonWork {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long personId;

    /** 逻辑关联 work_db.works.id，不跨服务引用实体 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workId;

    private String roleType;
}
