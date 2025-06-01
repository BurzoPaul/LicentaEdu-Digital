package com.utcn.edu_digital.MediaFiles;

import com.utcn.edu_digital.posts.Posts;
import com.utcn.edu_digital.posts.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/media")
@CrossOrigin(origins = "*")
public class MediaFilesController {

    @Autowired
    private MediaFilesService mediaFilesService;

    @Autowired
    private PostsService postsService;

    /**
     * Șterge un fișier media după ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMediaFile(@PathVariable int id) {
        try {
            boolean isDeleted = mediaFilesService.deleteMediaFileById(id);
            if (isDeleted) {
                return ResponseEntity.ok("Media file deleted successfully.");
            } else {
                return ResponseEntity.status(404).body("Media file not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting media file: " + e.getMessage());
        }
    }

    /**
     * Șterge toate fișierele media asociate unei postări
     */
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<String> deleteAllMediaForPost(@PathVariable int postId) {
        try {
            Optional<Posts> postOpt = postsService.getPostById(postId);
            if (postOpt.isPresent()) {
                mediaFilesService.deleteAllMediaForPost(postOpt.get());
                return ResponseEntity.ok("All media files for post deleted successfully.");
            } else {
                return ResponseEntity.status(404).body("Post not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting media files: " + e.getMessage());
        }
    }

    /**
     * Returnează metadata fișierelor media pentru o postare (fără datele binare)
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<MediaFilesService.MediaFilesDto>> getMediaMetadataForPost(@PathVariable int postId) {
        try {
            List<MediaFilesService.MediaFilesDto> mediaFiles = mediaFilesService.getMediaFilesMetadataByPostId(postId);
            return ResponseEntity.ok(mediaFiles);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Returnează metadata unui fișier media după ID (fără datele binare)
     */
    @GetMapping("/{id}/info")
    public ResponseEntity<MediaFilesService.MediaFilesDto> getMediaFileInfo(@PathVariable int id) {
        Optional<MediaFiles> mediaFileOpt = mediaFilesService.getMediaFileById(id);
        if (mediaFileOpt.isPresent()) {
            MediaFiles mediaFile = mediaFileOpt.get();
            MediaFilesService.MediaFilesDto dto = new MediaFilesService.MediaFilesDto(
                    mediaFile.getId(),
                    mediaFile.getName(),
                    mediaFile.getType(),
                    mediaFile.getSize()
            );
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    /**
     * Descarcă fișierul media actual (returnează datele binare)
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadMediaFile(@PathVariable int id) {
        try {
            Optional<MediaFiles> mediaFileOpt = mediaFilesService.getMediaFileById(id);
            if (mediaFileOpt.isPresent()) {
                MediaFiles mediaFile = mediaFileOpt.get();

                if (mediaFile.getData() != null) {
                    String contentType = mediaFile.getType();
                    if (contentType == null) {
                        contentType = "application/octet-stream";
                    }

                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment; filename=\"" + mediaFile.getName() + "\"")
                            .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(mediaFile.getSize()))
                            .body(mediaFile.getData());
                } else {
                    return ResponseEntity.status(404).body(null);
                }
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Afișează fișierul media în browser (pentru imagini/video)
     */
    @GetMapping("/{id}/view")
    public ResponseEntity<byte[]> viewMediaFile(@PathVariable int id) {
        try {
            Optional<MediaFiles> mediaFileOpt = mediaFilesService.getMediaFileById(id);
            if (mediaFileOpt.isPresent()) {
                MediaFiles mediaFile = mediaFileOpt.get();

                if (mediaFile.getData() != null) {
                    String contentType = mediaFile.getType();
                    if (contentType == null) {
                        contentType = "application/octet-stream";
                    }

                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(mediaFile.getSize()))
                            .body(mediaFile.getData());
                } else {
                    return ResponseEntity.status(404).body(null);
                }
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Găsește fișiere media după tip
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<MediaFilesService.MediaFilesDto>> getMediaByType(@PathVariable String type) {
        try {
            List<MediaFiles> mediaFiles = mediaFilesService.getMediaFilesByType(type);
            List<MediaFilesService.MediaFilesDto> dtoList = mediaFiles.stream()
                    .map(mf -> new MediaFilesService.MediaFilesDto(mf.getId(), mf.getName(), mf.getType(), mf.getSize()))
                    .toList();
            return ResponseEntity.ok(dtoList);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Numără fișierele media pentru o postare
     */
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> countMediaForPost(@PathVariable int postId) {
        try {
            long count = mediaFilesService.countMediaFilesByPostId(postId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}