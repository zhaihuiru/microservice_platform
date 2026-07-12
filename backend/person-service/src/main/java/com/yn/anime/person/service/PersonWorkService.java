package com.yn.anime.person.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yn.anime.person.entity.PersonWork;
import com.yn.anime.person.vo.PersonWorkVO;
import com.yn.anime.person.vo.WorkPersonVO;

import java.util.List;

public interface PersonWorkService extends IService<PersonWork> {

    boolean removeByCompositeKey(Long personId, Long workId, String roleType);

    PersonWork getByCompositeKey(Long personId, Long workId, String roleType);

    List<PersonWork> listByPersonId(Long personId);

    List<PersonWork> listByWorkId(Long workId);

    Page<PersonWork> pagePersonWorks(long current, long size, Long personId, Long workId, String roleType);

    List<PersonWorkVO> listWorksByPerson(
            Long personId
    );

    List<WorkPersonVO> listPersonsByWork(
            Long workId
    );

    boolean createRelation(PersonWork personWork);
}
