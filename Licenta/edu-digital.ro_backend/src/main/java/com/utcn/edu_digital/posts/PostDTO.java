package com.utcn.edu_digital.posts;

import lombok.Getter;

import java.util.List;

@Getter
public class PostDTO {
    private int id;
    private String titlu;
    private String imagineUrl;
    private String videoUrl;

    public PostDTO(Posts post) {
        this.id = post.getId();
        this.titlu = post.getTitle();

        // primul fișier media (imagine)
        if (post.getMediaFiles() != null && !post.getMediaFiles().isEmpty()) {
            int mediaId = post.getMediaFiles().get(0).getId();
            this.imagineUrl = "http://localhost:8080/media/" + mediaId + "/view";
        } else {
            this.imagineUrl = "";
        }

        // videoUrl (poate fi null sau gol dacă nu există)
        this.videoUrl = post.getVideoUrl() != null
                ? post.getVideoUrl()
                : "";
    }
}
