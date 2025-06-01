package com.utcn.edu_digital.MediaFiles;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.utcn.edu_digital.posts.Posts;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "size")
    private long size;

    // Datele binare ale fișierului - salvate în baza de date
    @Lob
    @Column(name = "data", columnDefinition = "LONGBLOB")
    @JsonIgnore // Excludem din serializarea JSON pentru a evita răspunsuri mari
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference
    private Posts post;

    // Constructor auxiliar fără data (pentru responses)
    public MediaFiles(int id, String name, String type, long size) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.size = size;
    }
}
