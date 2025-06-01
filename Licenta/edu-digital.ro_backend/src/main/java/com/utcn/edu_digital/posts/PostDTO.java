package com.utcn.edu_digital.posts;

import lombok.Getter;

@Getter
public class PostDTO {
    private int id;
    private String titlu;
    private String imagineUrl;

    public PostDTO(Posts post) {
        this.id = post.getId();
        this.titlu = post.getTitle();
        if (post.getMediaFiles() != null && !post.getMediaFiles().isEmpty()) {
            int mediaId = post.getMediaFiles().get(0).getId();
            this.imagineUrl = "http://localhost:8080/media/" + mediaId + "/view";
        } else {
            this.imagineUrl = "";
        }
    }

}
