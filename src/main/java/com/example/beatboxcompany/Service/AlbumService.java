package com.example.beatboxcompany.Service;

import com.example.beatboxcompany.Dto.AlbumDto;
import com.example.beatboxcompany.Request.AlbumRequest;
import java.util.List;

public interface AlbumService {
    AlbumDto createPendingAlbum(AlbumRequest request, String currentUserId);
    List<AlbumDto> getAlbumsByStatus(String status);
    AlbumDto approveAlbum(String albumId);
    AlbumDto rejectAlbum(String albumId, String reason);
    AlbumDto getPublishedAlbumById(String albumId);
    List<AlbumDto> getAlbumsByUploader(String currentUserId);
    AlbumDto updateAlbumStatus(String albumId, String status, String reason);
}