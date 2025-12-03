package com.example.beatboxcompany.Dto;

import lombok.Data;

import java.util.List;

@Data
public class SongDto {
    private String id;
    private String title;
    private int duration;
    private String streamUrl;
    private String coverUrl;
    private String artistId; 
    private String albumId;
    private List<String> genre;
    private long viewCount;
    private String status;
    private Boolean isExplicit;
}