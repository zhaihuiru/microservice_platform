package com.yn.anime.work.vo;

import com.yn.anime.work.entity.Category;
import com.yn.anime.work.entity.Work;
import lombok.Data;

import java.util.List;

/*
    classDescription:
*/
@Data
public class WorkDetailVO {

    private Work work;

    private List<Category> categories;
}