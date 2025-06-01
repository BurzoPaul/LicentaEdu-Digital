package com.utcn.edu_digital.posts;


import com.utcn.edu_digital.MediaFiles.MediaFiles;
import com.utcn.edu_digital.MediaFiles.MediaFilesRepository;
import com.utcn.edu_digital.user.User;
import com.utcn.edu_digital.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PostsService {
    // Tipuri de fișiere permise
    private final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/avi", "video/mov", "video/wmv", "video/webm"
    );

    // Dimensiune maximă pentru fișiere (50MB)
    private final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private MediaFilesRepository mediaFilesRepository;
    @Autowired
    private UserRepository userRepository;

    public Posts savePost(String title, String description, List<MultipartFile> files, int userId) throws IOException {
        // Validări
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Creăm postarea și o salvăm
        Posts post = new Posts();
        post.setTitle(title.trim());
        post.setDescription(description != null ? description.trim() : "");
        post.setUser(user);
        post = postsRepository.save(post);

        // Salvăm fișierele în baza de date
        List<MediaFiles> mediaFiles = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // Validăm fișierul
                    validateFile(file);

                    // Creăm obiectul MediaFile cu datele binare
                    MediaFiles mediaFile = new MediaFiles();
                    mediaFile.setName(file.getOriginalFilename());
                    mediaFile.setType(file.getContentType());
                    mediaFile.setSize(file.getSize());
                    mediaFile.setData(file.getBytes()); // Salvăm datele binare
                    mediaFile.setPost(post);

                    // Salvăm fișierul în baza de date
                    mediaFiles.add(mediaFilesRepository.save(mediaFile));
                }
            }
        }

        // Asociem fișierele cu postarea și salvăm din nou postarea
        post.setMediaFiles(mediaFiles);
        return postsRepository.save(post);
    }

    /**
     * Validează un fișier înainte de salvare
     */
    private void validateFile(MultipartFile file) throws IOException {
        // Verificăm dimensiunea fișierului
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 50MB");
        }

        // Verificăm tipul fișierului
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("File type cannot be determined");
        }

        boolean isValidType = ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase()) ||
                ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase());

        if (!isValidType) {
            throw new IllegalArgumentException("File type not allowed: " + contentType);
        }

        // Verificăm numele fișierului
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }
    }

    /**
     * Returnează o postare după ID, dacă există.
     */
    public Optional<Posts> getPostById(int id) {
        return postsRepository.findById(id);
    }

    /**
     * Returnează toate postările.
     */
    public List<Posts> getAllPosts() {
        return postsRepository.findAll();
    }

    /**
     * Șterge o postare și fișierele asociate.
     */
    public void deletePost(int id) {
        Optional<Posts> postOptional = postsRepository.findById(id);
        if (postOptional.isPresent()) {
            Posts post = postOptional.get();

            // Ștergem postarea și fișierele asociate (cascade delete va șterge automat fișierele)
            postsRepository.delete(post);
        }
    }

    /**
     * Actualizează o postare existentă
     */
    public Posts updatePost(int id, String title, String description) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        if (title != null && !title.trim().isEmpty()) {
            post.setTitle(title.trim());
        }

        if (description != null) {
            post.setDescription(description.trim());
        }

        return postsRepository.save(post);
    }

    /**
     * Adaugă fișiere media la o postare existentă
     */
    public Posts addMediaToPost(int postId, List<MultipartFile> files) throws IOException {
        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        List<MediaFiles> existingMediaFiles = post.getMediaFiles();
        if (existingMediaFiles == null) {
            existingMediaFiles = new ArrayList<>();
        }

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    validateFile(file);

                    MediaFiles mediaFile = new MediaFiles();
                    mediaFile.setName(file.getOriginalFilename());
                    mediaFile.setType(file.getContentType());
                    mediaFile.setSize(file.getSize());
                    mediaFile.setData(file.getBytes());
                    mediaFile.setPost(post);

                    existingMediaFiles.add(mediaFilesRepository.save(mediaFile));
                }
            }
        }

        post.setMediaFiles(existingMediaFiles);
        return postsRepository.save(post);
    }
}
