package com.utcn.edu_digital.MediaFiles;

import com.utcn.edu_digital.posts.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaFilesRepository extends JpaRepository<MediaFiles, Integer> {

    /**
     * Găsește toate fișierele media pentru o postare specifică
     */
    List<MediaFiles> findByPostId(int postId);

    /**
     * Găsește fișierele media după tip
     */
    List<MediaFiles> findByType(String type);

    /**
     * Găsește fișierele media după postare și tip
     */
    List<MediaFiles> findByPostIdAndType(int postId, String type);

    /**
     * Numără fișierele media pentru o postare specifică
     */
    long countByPostId(int postId);

    /**
     * Găsește fișierele media pentru o postare (alternativă cu obiect Posts)
     */
    List<MediaFiles> findByPost(Posts post);

    /**
     * Numără fișierele media pentru un anumit tip
     */
    long countByType(String type);

    /**
     * Găsește fișierele media cu numele specificat
     */
    List<MediaFiles> findByName(String name);

    /**
     * Găsește fișierele media cu dimensiunea mai mare decât valoarea specificată
     */
    List<MediaFiles> findBySizeGreaterThan(long size);

    /**
     * Găsește fișierele media cu dimensiunea mai mică decât valoarea specificată
     */
    List<MediaFiles> findBySizeLessThan(long size);

    /**
     * Query personalizat pentru a găsi fișierele media cu metadata (fără datele binare)
     * Aceasta este optimizată pentru performanță când nu ai nevoie de datele binare
     */
    @Query("SELECT new MediaFiles(m.id, m.name, m.type, m.size) FROM MediaFiles m WHERE m.post.id = :postId")
    List<MediaFiles> findMetadataByPostId(@Param("postId") int postId);

    /**
     * Query personalizat pentru a găsi doar ID-urile fișierelor media pentru o postare
     */
    @Query("SELECT m.id FROM MediaFiles m WHERE m.post.id = :postId")
    List<Integer> findIdsByPostId(@Param("postId") int postId);

    /**
     * Verifică dacă există fișiere media pentru o postare specifică
     */
    boolean existsByPostId(int postId);

    /**
     * Șterge toate fișierele media pentru o postare specifică
     */
    void deleteByPostId(int postId);

    /**
     * Șterge toate fișierele media pentru un anumit tip
     */
    void deleteByType(String type);
}
