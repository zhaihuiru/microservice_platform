package com.yn.anime.person.client;

import com.yn.anime.person.dto.CharacterActorWorkDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/*
    classDescription:
*/
@FeignClient(name="character-service")
public interface CharacterClient {



    @GetMapping(
            "/internal/character-actors/person/{personId}/works"
    )
    List<CharacterActorWorkDTO> listByPerson(
            @PathVariable("personId")
            Long personId
    );

}