package com.yn.anime.person.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/*
    classDescription:
*/
@Data
public class CharacterActorWorkDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long workId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long characterId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long personId;

    private String roleType;

}