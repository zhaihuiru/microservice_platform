package com.yn.anime.favorite.repository;

import com.yn.anime.favorite.entity.FavoriteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 收藏项 Repository
 */
@Repository
public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Long> {

    /**
     * 根据收藏夹ID查询所有收藏项
     */
    List<FavoriteItem> findByFolderIdOrderByCreatedAtDesc(Long folderId);

    /**
     * 根据收藏夹ID和作品ID查找收藏项
     */
    Optional<FavoriteItem> findByFolderIdAndWorkId(Long folderId, Long workId);

    /**
     * 根据收藏夹ID删除所有收藏项
     */
    @Modifying
    @Transactional
    void deleteByFolderId(Long folderId);

    /**
     * 根据收藏夹ID和作品ID删除收藏项
     */
    @Modifying
    @Transactional
    void deleteByFolderIdAndWorkId(Long folderId, Long workId);
}
