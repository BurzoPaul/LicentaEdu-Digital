package com.utcn.edu_digital.posts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Integer> {
    List<Posts> findAllByUserId(int userId);

    @Query("SELECT p FROM Posts p " +
            "WHERE (:keyword IS NULL OR " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.tags) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Posts> searchFilteredPosts(@Param("title") String title,
                                    @Param("author") String author,
                                    @Param("keyword") String keyword);



    @Query("SELECT p FROM Posts p " +
            "WHERE (:tag IS NULL OR LOWER(p.tags) LIKE LOWER(CONCAT('%', :tag, '%')))")
    List<Posts> findByTag(@Param("tag") String tag);



}
