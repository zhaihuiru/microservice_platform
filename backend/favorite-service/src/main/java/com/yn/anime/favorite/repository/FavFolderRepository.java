package com.yn.anime.favorite.repository;

import com.yn.anime.favorite.entity.FavFolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 收藏夹 Repository
 */
@Repository
public interface FavFolderRepository extends JpaRepository<FavFolder, Long> {

    /**
     * 根据用户ID查询所有收藏夹
     */
    List<FavFolder> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据用户ID查询公开的收藏夹
     */
    List<FavFolder> findByUserIdAndIsPublicTrueOrderByCreatedAtDesc(Long userId);

    /**
     * 根据收藏夹ID和用户ID查找
     */
    java.util.Optional<FavFolder> findByIdAndUserId(Long id, Long userId);
}
