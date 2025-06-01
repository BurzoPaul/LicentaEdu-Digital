package com.utcn.edu_digital.MediaFiles;

import com.utcn.edu_digital.posts.Posts;
import com.utcn.edu_digital.posts.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MediaFilesService {
    @Autowired
    private MediaFilesRepository mediaFilesRepository;

    @Autowired
    private PostsRepository postsRepository;

    /**
     * Șterge un fișier media după ID
     */
    public boolean deleteMediaFileById(int id) {
        if (mediaFilesRepository.existsById(id)) {
            mediaFilesRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Șterge toate fișierele media asociate unei postări
     */
    public void deleteAllMediaForPost(Posts post) {
        if (post != null && post.getMediaFiles() != null) {
            // Ștergem din baza de date (nu mai trebuie să ștergem de pe disc)
            mediaFilesRepository.deleteAll(post.getMediaFiles());
        }
    }

    /**
     * Găsește toate fișierele media pentru o postare (fără datele binare)
     */
    public List<MediaFiles> getMediaFilesByPostId(int postId) {
        return mediaFilesRepository.findByPostId(postId);
    }

    /**
     * Găsește un fișier media după ID (cu datele binare pentru download)
     */
    public Optional<MediaFiles> getMediaFileById(int id) {
        return mediaFilesRepository.findById(id);
    }

    /**
     * Găsește fișierele media pentru o postare, fără datele binare (pentru listare)
     */
    public List<MediaFilesDto> getMediaFilesMetadataByPostId(int postId) {
        List<MediaFiles> mediaFiles = mediaFilesRepository.findByPostId(postId);
        return mediaFiles.stream()
                .map(mf -> new MediaFilesDto(mf.getId(), mf.getName(), mf.getType(), mf.getSize()))
                .toList();
    }

    /**
     * Salvează un fișier media
     */
    public MediaFiles saveMediaFile(MediaFiles mediaFile) {
        return mediaFilesRepository.save(mediaFile);
    }

    /**
     * Verifică dacă un fișier media există
     */
    public boolean existsById(int id) {
        return mediaFilesRepository.existsById(id);
    }

    /**
     * Găsește fișierele media după tip
     */
    public List<MediaFiles> getMediaFilesByType(String type) {
        return mediaFilesRepository.findByType(type);
    }

    /**
     * Găsește fișierele media după tip pentru o postare specifică
     */
    public List<MediaFiles> getMediaFilesByPostIdAndType(int postId, String type) {
        return mediaFilesRepository.findByPostIdAndType(postId, type);
    }

    /**
     * Numără fișierele media pentru o postare
     */
    public long countMediaFilesByPostId(int postId) {
        return mediaFilesRepository.countByPostId(postId);
    }

    /**
     * DTO class pentru metadata fișierelor media (fără datele binare)
     */
    public static class MediaFilesDto {
        private int id;
        private String name;
        private String type;
        private long size;

        public MediaFilesDto(int id, String name, String type, long size) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.size = size;
        }

        // Getters și setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
    }
}