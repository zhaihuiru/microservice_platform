package com.yn.anime.person.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yn.anime.common.dto.WorkSimpleDTO;
import com.yn.anime.person.client.CharacterClient;
import com.yn.anime.person.client.WorkClient;
import com.yn.anime.person.dto.CharacterActorWorkDTO;
import com.yn.anime.person.entity.PersonWork;
import com.yn.anime.person.mapper.PersonWorkMapper;
import com.yn.anime.person.service.PersonWorkService;
import com.yn.anime.person.vo.PersonWorkVO;
import com.yn.anime.person.vo.WorkPersonVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonWorkServiceImpl extends ServiceImpl<PersonWorkMapper, PersonWork> implements PersonWorkService {

    private final WorkClient workClient;
    private final CharacterClient characterClient;

    @Override
    public boolean removeByCompositeKey(Long personId, Long workId, String roleType) {
        LambdaQueryWrapper<PersonWork> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PersonWork::getPersonId, personId)
                .eq(PersonWork::getWorkId, workId)
                .eq(PersonWork::getRoleType, roleType);
        return remove(wrapper);
    }

    @Override
    public PersonWork getByCompositeKey(Long personId, Long workId, String roleType) {
        LambdaQueryWrapper<PersonWork> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PersonWork::getPersonId, personId)
                .eq(PersonWork::getWorkId, workId)
                .eq(PersonWork::getRoleType, roleType);
        return getOne(wrapper);
    }

    @Override
    public List<PersonWork> listByPersonId(Long personId) {
        LambdaQueryWrapper<PersonWork> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PersonWork::getPersonId, personId);
        return list(wrapper);
    }

    @Override
    public List<PersonWork> listByWorkId(Long workId) {
        LambdaQueryWrapper<PersonWork> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PersonWork::getWorkId, workId);
        return list(wrapper);
    }

    @Override
    public Page<PersonWork> pagePersonWorks(long current, long size, Long personId, Long workId, String roleType) {
        LambdaQueryWrapper<PersonWork> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(personId != null, PersonWork::getPersonId, personId)
                .eq(workId != null, PersonWork::getWorkId, workId)
                .eq(StringUtils.hasText(roleType), PersonWork::getRoleType, roleType);
        return page(new Page<>(current, size), wrapper);
    }

//    @Override
//    public List<PersonWorkVO> listWorksByPerson(
//            Long personId) {
//
//        return baseMapper.listWorksByPerson(
//                personId
//        );
//    }

    @Override
    public List<PersonWorkVO> listWorksByPerson(Long personId) {
        List<PersonWorkVO> result = new ArrayList<>();

    /*
        第一部分：
        查询导演 作者 编剧
     */
        List<PersonWork> personWorks =
                lambdaQuery()
                        .eq(
                                PersonWork::getPersonId,
                                personId
                        )
                        .list();

        if(!personWorks.isEmpty()){
            List<Long> ids =
                    personWorks.stream()
                            .map(PersonWork::getWorkId)
                            .toList();
            List<WorkSimpleDTO> works =
                    workClient.batch(ids);
            for(PersonWork pw:personWorks){
                PersonWorkVO vo =
                        new PersonWorkVO();
                vo.setWorkId(
                        pw.getWorkId()
                );
                vo.setRoleType(
                        pw.getRoleType()
                );
                vo.setSource(
                        "幕后人员"
                );
                works.stream()
                        .filter(
                                w -> w.getId()
                                        .equals(pw.getWorkId())
                        )
                        .findFirst()
                        .ifPresent(
                                w ->
                                        vo.setWorkTitle(
                                                w.getTitle()
                                        )
                        );
                result.add(vo);
            }
        }
    /*
        第二部分：
        查询声优演员
     */
        List<CharacterActorWorkDTO> actors =
                characterClient.listByPerson(
                        personId
                );
        if(!actors.isEmpty()){
            List<Long> ids =
                    actors.stream()
                            .map(
                                    CharacterActorWorkDTO::getWorkId
                            )
                            .toList();
            List<WorkSimpleDTO> works =
                    workClient.batch(ids);
            for(CharacterActorWorkDTO actor:actors){
                PersonWorkVO vo =
                        new PersonWorkVO();
                vo.setWorkId(
                        actor.getWorkId()
                );
                vo.setCharacterId(
                        actor.getCharacterId()
                );
                vo.setRoleType(
                        actor.getRoleType()
                );
                vo.setSource(
                        "声优演员"
                );
                works.stream()
                        .filter(
                                w ->
                                        w.getId()
                                                .equals(actor.getWorkId())
                        )
                        .findFirst()
                        .ifPresent(
                                w ->
                                        vo.setWorkTitle(
                                                w.getTitle()
                                        )
                        );
                result.add(vo);
            }
        }
        return result;
    }
    @Override
    public List<WorkPersonVO> listPersonsByWork(
            Long workId) {

        return baseMapper.listPersonsByWork(
                workId
        );
    }

    @Override
    public boolean createRelation(
            PersonWork personWork) {

        Boolean exists =
                workClient.exist(
                        personWork.getWorkId()
                );

        if (Boolean.FALSE.equals(exists)) {

            throw new RuntimeException(
                    "作品不存在"
            );
        }

        return save(personWork);
    }
}
