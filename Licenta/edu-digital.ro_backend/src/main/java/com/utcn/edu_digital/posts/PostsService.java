package com.utcn.edu_digital.posts;

import com.utcn.edu_digital.MediaFiles.MediaFiles;
import com.utcn.edu_digital.MediaFiles.MediaFilesRepository;
import com.utcn.edu_digital.email.PostEmailLogDto;
import com.utcn.edu_digital.user.User;
import com.utcn.edu_digital.posts.PostEmailLogRepository;
import com.utcn.edu_digital.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PostsService {
    private final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/avi", "video/mov", "video/wmv", "video/webm"
    );

    private final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private MediaFilesRepository mediaFilesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostEmailLogRepository postEmailLogRepository;


    public Posts savePost(
            String title,
            String description,
            String videoUrl,
            List<MultipartFile> files,
            int userId,
            String tags
    ) throws IOException {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Posts post = new Posts();
        post.setTitle(title.trim());
        post.setDescription(description != null ? description.trim() : "");
        post.setVideoUrl(videoUrl != null ? videoUrl.trim() : null);
        post.setTags(tags != null ? tags.trim() : null);
        post.setUser(user);
        post = postsRepository.save(post);

        List<MediaFiles> mediaFiles = new ArrayList<>();
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

                    mediaFiles.add(mediaFilesRepository.save(mediaFile));
                }
            }
        }

        post.setMediaFiles(mediaFiles);
        return postsRepository.save(post);
    }

    private void validateFile(MultipartFile file) throws IOException {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 50MB");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("File type cannot be determined");
        }

        boolean isValidType = ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase()) ||
                ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase());

        if (!isValidType) {
            throw new IllegalArgumentException("File type not allowed: " + contentType);
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }
    }

    public Optional<Posts> getPostById(int id) {
        return postsRepository.findById(id);
    }

    public List<Posts> getAllPosts() {
        return postsRepository.findAll();
    }

    public List<Posts> getPostsByUserId(int userId) {
        return postsRepository.findAllByUserId(userId);
    }

    public void deletePost(int id) {
        Optional<Posts> postOptional = postsRepository.findById(id);
        postOptional.ifPresent(postsRepository::delete);
    }

    public Posts updatePost(
            int id,
            String title,
            String description,
            String videoUrl,
            String tags
    ) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (title != null) post.setTitle(title.trim());
        if (description != null) post.setDescription(description.trim());
        if (videoUrl != null) post.setVideoUrl(videoUrl.trim());
        if (tags != null) post.setTags(tags.trim());

        return postsRepository.save(post);
    }

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

    public List<PostDetailsDto> searchPosts(String title, String author, String keyword) {
        List<Posts> filteredPosts = postsRepository.searchFilteredPosts(title, author, keyword);

        return filteredPosts.stream()
                .map(post -> {
                    List<String> imageUrls = post.getMediaFiles().stream()
                            .map(mf -> "http://localhost:8080/media/" + mf.getId() + "/view")
                            .collect(Collectors.toList());

                    return new PostDetailsDto(
                            post.getId(),
                            post.getTitle(),
                            post.getDescription(),
                            post.getUser().getId(),
                            post.getUser().getName(),
                            imageUrls,
                            post.getVideoUrl(),
                            post.getTags()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<Posts> getPostsByIds(List<Integer> ids) {
        return postsRepository.findAllById(ids);
    }

    public List<PostEmailLogDto> getEmailLogsForUser(int userId) {
        List<Posts> userPosts = postsRepository.findAllByUserId(userId);

        return userPosts.stream()
                .flatMap(post ->
                        postEmailLogRepository.findByPostId(post.getId()).stream()
                                .map(log -> new PostEmailLogDto(post.getId(), log.getRecipientEmail()))
                )
                .collect(Collectors.toList());
    }

}
