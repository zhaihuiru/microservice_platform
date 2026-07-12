package com.yn.anime.work.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
@TableName("work_category")
public class WorkCategory {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long workId;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;
}
