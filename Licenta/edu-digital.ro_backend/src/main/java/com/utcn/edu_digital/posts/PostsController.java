package com.utcn.edu_digital.posts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
public class PostsController {
    @Autowired
    private PostsService postsService;

    /**
     * Creează o postare nouă cu fișiere media
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestParam String title,
                                        @RequestParam String description,
                                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                        @RequestParam int userId) {
        try {
            Posts post = postsService.savePost(title, description, files, userId);
            return ResponseEntity.ok(post);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing files: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    /**
     * Returnează toate postările
     */
    @GetMapping("/all")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        try {
            List<Posts> posts = postsService.getAllPosts();
            List<PostDTO> postDTOs = posts.stream()
                    .map(PostDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(postDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


    /**
     * Returnează o postare după ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Posts> getPostById(@PathVariable int id) {
        try {
            Optional<Posts> post = postsService.getPostById(id);
            if (post.isPresent()) {
                return ResponseEntity.ok(post.get());
            } else {
                return ResponseEntity.status(404).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Șterge o postare după ID
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable int id) {
        try {
            postsService.deletePost(id);
            return ResponseEntity.ok("Post deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting post: " + e.getMessage());
        }
    }

    /**
     * Actualizează o postare existentă (doar titlu și descriere)
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePost(@PathVariable int id,
                                        @RequestParam(required = false) String title,
                                        @RequestParam(required = false) String description) {
        try {
            Posts updatedPost = postsService.updatePost(id, title, description);
            return ResponseEntity.ok(updatedPost);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    /**
     * Adaugă fișiere media la o postare existentă
     */
    @PostMapping("/{id}/add-media")
    public ResponseEntity<?> addMediaToPost(@PathVariable int id,
                                            @RequestParam("files") List<MultipartFile> files) {
        try {
            Posts updatedPost = postsService.addMediaToPost(id, files);
            return ResponseEntity.ok(updatedPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation error: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing files: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    /**
     * Returnează postările pentru un utilizator specific
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Posts>> getPostsByUserId(@PathVariable int userId) {
        try {
            // Această funcționalitate ar trebui adăugată în PostsService
            // List<Posts> posts = postsService.getPostsByUserId(userId);
            // return ResponseEntity.ok(posts);
            return ResponseEntity.status(501).build(); // Not implemented yet
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}