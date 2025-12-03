// Dto/AlbumDto.java
package com.example.beatboxcompany.Dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class AlbumDto {
    private String id;
    private String title;
    private LocalDate releaseDate;
    private String coverUrl;
    private String artistId;
    private String artistName;
    private String status;
    private String rejectedReason;
    private Integer totalTracks;
    private Long totalDurationMs;
    private List<TrackDto> tracks;
}