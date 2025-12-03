package com.example.beatboxcompany.Entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "songs")
@Data
public class Song {
    @Id
    private String id;

    private String title;
    private String artistId;
    private String albumId;

    private int duration;  
    private Long durationMs;                  // đơn vị: giây (frontend thường dùng int)
    private String streamUrl;
    private String coverUrl;

    private List<String> genre = new ArrayList<>();
    private long viewCount = 0L;
    private Boolean isExplicit = false;
    private String status = "PENDING";

    private LocalDateTime createdAt = LocalDateTime.now();
}