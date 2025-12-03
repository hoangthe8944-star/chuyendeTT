package com.example.beatboxcompany.Request;

import lombok.Data;
import java.util.List;

@Data
public class SongUploadRequest {
    private String artistId;
    private String title;
    private int duration;                    // giây
    private String streamUrl;
    private String coverUrl;
    private String albumId;                  // có thể null nếu là single
    private List<String> genre;
    private Boolean isExplicit = false;
}