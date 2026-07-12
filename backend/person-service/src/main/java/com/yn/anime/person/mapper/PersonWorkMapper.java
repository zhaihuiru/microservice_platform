package com.yn.anime.person.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yn.anime.person.entity.PersonWork;
import com.yn.anime.person.vo.PersonWorkVO;
import com.yn.anime.person.vo.WorkPersonVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PersonWorkMapper extends BaseMapper<PersonWork> {
    @Select("""
        SELECT
            work_id AS workId,
            role_type AS roleType
        FROM person_work
        WHERE person_id = #{personId}
        """)
    List<PersonWorkVO> listWorksByPerson(
            @Param("personId") Long personId
    );

    @Select("""
        SELECT
            p.id AS personId,
            p.name AS personName,
            pw.role_type AS roleType
        FROM person_work pw
        JOIN persons p
            ON pw.person_id = p.id
        WHERE pw.work_id = #{workId}
        """)
    List<WorkPersonVO> listPersonsByWork(
            @Param("workId") Long workId
    );
}
