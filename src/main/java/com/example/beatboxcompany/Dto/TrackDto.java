package com.example.beatboxcompany.Dto;

import lombok.Data;

@Data
public class TrackDto {
    private String songId;       // ID của bài hát trong DB
    private String title;
    private Integer trackNumber; // thứ tự trong album (nếu có)
    private Long durationMs;
    private Boolean isExplicit = false;
}