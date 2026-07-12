package com.yn.anime.person.vo;

import com.yn.anime.person.entity.Person;
import lombok.Data;

import java.util.List;

/*
    classDescription:
*/
@Data
public class PersonDetailVO {

    private Person person;

    private List<PersonWorkVO> works;
}