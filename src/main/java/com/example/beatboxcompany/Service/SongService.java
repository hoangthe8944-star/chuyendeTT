package com.example.beatboxcompany.Service;

import com.example.beatboxcompany.Dto.SongDto;
import com.example.beatboxcompany.Request.SongUploadRequest;

import java.util.List;

public interface SongService {
    
    // 1. Artist tạo bài hát mới (Status: PENDING)
    SongDto createPendingSong(SongUploadRequest request, String currentUserId);
    
    // 2. Lấy bài hát chờ duyệt (Admin)
    List<SongDto> getSongsByStatus(String status);
    
    // 3. Admin duyệt/từ chối
    SongDto updateStatus(String songId, String newStatus);

    // 4. Lấy danh sách bài hát của Artist (bao gồm cả PENDING)
    List<SongDto> getSongsByUploader(String currentUserId);

    // 5. Lấy bài hát cho Public/Streaming (Status: PUBLISHED)
    SongDto getPublishedSongById(String songId);
    
    // 6. Tăng lượt xem (khi user bắt đầu nghe)
    SongDto incrementViewCount(String songId);
    
    // 7. Lấy bài hát trending
    List<SongDto> getTrendingPublishedSongs(int limit);
    
}