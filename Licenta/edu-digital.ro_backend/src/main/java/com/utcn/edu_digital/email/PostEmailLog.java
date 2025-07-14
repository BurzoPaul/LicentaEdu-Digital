package com.utcn.edu_digital.posts;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostEmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private int postId;

    @Column(name = "recipient_email", nullable = false, length = 255)
    private String recipientEmail;

    // Constructor auxiliar pentru salvare rapidă
    public PostEmailLog(int postId, String recipientEmail) {
        this.postId = postId;
        this.recipientEmail = recipientEmail;
    }
}
