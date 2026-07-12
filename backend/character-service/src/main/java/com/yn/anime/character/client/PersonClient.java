package com.yn.anime.character.client;

import com.yn.anime.common.dto.PersonSimpleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/*
    classDescription:
*/
@FeignClient(name = "person-service")
public interface PersonClient {

   @GetMapping("/internal/persons/simple/{id}")
   PersonSimpleDTO getSimple(
           @PathVariable("id") Long id
   );

   @PostMapping("/internal/persons/batch")
   List<PersonSimpleDTO> batch(
           @RequestBody List<Long> ids
   );
}