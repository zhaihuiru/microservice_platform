package com.yn.anime.person.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yn.anime.person.entity.Person;
import com.yn.anime.person.entity.PersonWork;
import com.yn.anime.person.vo.PersonDetailVO;

public interface PersonService extends IService<Person> {

    Page<Person> pagePersons(long current, long size, String name, String country);
    Page<Person> searchPersons(
            long current,
            long size,
            String keyword
    );

    PersonDetailVO getPersonDetail(
            Long personId
    );

    Boolean exists(Long id);

    Person getPersonWithFullUrl(Long id);
}
