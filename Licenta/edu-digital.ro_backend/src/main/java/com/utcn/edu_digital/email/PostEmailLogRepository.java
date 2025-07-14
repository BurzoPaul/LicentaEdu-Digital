package com.utcn.edu_digital.posts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.utcn.edu_digital.posts.PostEmailLog;

import java.util.List;

@Repository
public interface PostEmailLogRepository extends JpaRepository<PostEmailLog, Long> {

    // Găsește toate înregistrările pentru o singură postare
    List<PostEmailLog> findByPostId(int postId);

    // Găsește toate înregistrările pentru o listă de postări
    List<PostEmailLog> findByPostIdIn(List<Integer> postIds);

    // Găsește toate înregistrările pentru o adresă de email (opțional)
    List<PostEmailLog> findByRecipientEmail(String email);

    @Query("SELECT p.recipientEmail FROM PostEmailLog p WHERE p.postId = :postId")
    List<String> findEmailsByPostId(@Param("postId") int postId);
}
