package com.utcn.edu_digital.posts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Integer> {
    List<Posts> findAllByUserId(int userId);
}
