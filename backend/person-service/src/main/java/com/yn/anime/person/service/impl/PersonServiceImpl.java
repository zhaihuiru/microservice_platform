package com.yn.anime.person.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yn.anime.person.entity.Person;
import com.yn.anime.person.mapper.PersonMapper;
import com.yn.anime.person.mapper.PersonWorkMapper;
import com.yn.anime.person.service.PersonService;
import com.yn.anime.person.service.PersonWorkService;
import com.yn.anime.person.vo.PersonDetailVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PersonServiceImpl extends ServiceImpl<PersonMapper, Person> implements PersonService {

    @Resource
    private PersonWorkMapper personWorkMapper;

    @Resource
    private PersonWorkService personWorkService;

    @Value("${file.access-prefix}")
    private String accessPrefix;

    @Override
    public Page<Person> pagePersons(long current, long size, String name, String country) {
        LambdaQueryWrapper<Person> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Person::getName, name)
                .eq(StringUtils.hasText(country), Person::getCountry, country)
                .orderByDesc(Person::getCreatedTime);
        Page<Person> resultPage = page(new Page<>(current, size), wrapper);

        // 联遍历，动态为人物照片拼接网络前缀
        if (resultPage.getRecords() != null) {
            resultPage.getRecords().forEach(item -> {
                if (item != null && StringUtils.hasText(item.getAvatarUrl())) {
                    if (!item.getAvatarUrl().startsWith("http://") && !item.getAvatarUrl().startsWith("https://")) {
                        item.setAvatarUrl(accessPrefix + item.getAvatarUrl());
                    }
                }
            });
        }
        return resultPage;
    }

    // 搜索人物
    @Override
    public Page<Person> searchPersons(
            long current,
            long size,
            String keyword) {

        LambdaQueryWrapper<Person> wrapper =
                Wrappers.lambdaQuery();

        wrapper.like(
                StringUtils.hasText(keyword),
                Person::getName,
                keyword
        );

        Page<Person> resultPage = page(new Page<>(current, size), wrapper);

        if (resultPage.getRecords() != null) {
            resultPage.getRecords().forEach(item -> {
                if (item != null && StringUtils.hasText(item.getAvatarUrl())) {
                    if (!item.getAvatarUrl().startsWith("http://") && !item.getAvatarUrl().startsWith("https://")) {
                        item.setAvatarUrl(accessPrefix + item.getAvatarUrl());
                    }
                }
            });
        }
        return resultPage;
    }

    @Override
    public PersonDetailVO getPersonDetail(
            Long id) {

        Person person =
                getById(id);

        if (person == null) {
            return null;
        }

        if (StringUtils.hasText(person.getAvatarUrl())) {
            if (!person.getAvatarUrl().startsWith("http://") && !person.getAvatarUrl().startsWith("https://")) {
                person.setAvatarUrl(accessPrefix + person.getAvatarUrl());
            }
        }

        PersonDetailVO vo =
                new PersonDetailVO();

        vo.setPerson(person);

        vo.setWorks(
                personWorkService
                        .listWorksByPerson(id)
        );

        return vo;
    }

    @Override
    public Boolean exists(Long id) {

        return lambdaQuery()
                .eq(Person::getId, id)
                .exists();
    }

    @Override
    public Person getPersonWithFullUrl(Long id) {
        Person person = this.getById(id);
        if (person != null && StringUtils.hasText(person.getAvatarUrl())) {
            if (!person.getAvatarUrl().startsWith("http://") && !person.getAvatarUrl().startsWith("https://")) {
                person.setAvatarUrl(accessPrefix + person.getAvatarUrl());
            }
        }
        return person;
    }
}
