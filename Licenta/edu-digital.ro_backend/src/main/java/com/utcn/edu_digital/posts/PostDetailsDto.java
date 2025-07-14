package com.utcn.edu_digital.posts;
import java.util.List;

public record PostDetailsDto(
        int id,
        String title,
        String description,
        int authorId,
        String authorName,
        List<String> imageUrls,
        String videoUrl,
        String tags
) {}
