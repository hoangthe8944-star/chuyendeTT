package com.example.beatboxcompany.Controller;

import com.example.beatboxcompany.Dto.AlbumDto; // Cần import
import com.example.beatboxcompany.Dto.ArtistDto;
import com.example.beatboxcompany.Dto.SongDto;
import com.example.beatboxcompany.Request.SongUploadRequest; // Cần import Request
import com.example.beatboxcompany.Request.AlbumRequest; // Cần import Request
import com.example.beatboxcompany.Service.AlbumService; // Bổ sung AlbumService
import com.example.beatboxcompany.Service.ArtistService;
import com.example.beatboxcompany.Service.SongService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artist")
@PreAuthorize("hasAnyRole('ARTIST', 'ADMIN')") // Bảo vệ Controller này
public class ArtistController {

    private final SongService songService;
    private final ArtistService artistService;
    private final AlbumService albumService; // 1. Khai báo AlbumService

    // Constructor để Inject (tiêm) các Service
    public ArtistController(SongService songService, ArtistService artistService, AlbumService albumService) {
        this.songService = songService;
        this.artistService = artistService;
        this.albumService = albumService; // 2. Gán giá trị AlbumService
    }

    // --- Quản lý Hồ sơ Artist (Profile) ---
    
    // (Giữ nguyên các API getMyProfile và updateMyProfile)

    // --- Quản lý Bài hát (Songs) ---
    
    @PostMapping("/songs/upload")
    public ResponseEntity<SongDto> uploadSong(@RequestBody SongUploadRequest request) {
        String currentUserId = "MOCK_USER_ID_FROM_TOKEN"; 
        SongDto newSong = songService.createPendingSong(request, currentUserId); 
        
        return new ResponseEntity<>(newSong, HttpStatus.CREATED);
    }
    
    @GetMapping("/songs/my-uploads")
    public ResponseEntity<List<SongDto>> getMyUploads() {
        String currentUserId = "MOCK_USER_ID_FROM_TOKEN";
        List<SongDto> mySongs = songService.getSongsByUploader(currentUserId); 
        return ResponseEntity.ok(mySongs);
    }
    
    // --- Quản lý Album ---
    
    /**
     * POST /api/artist/albums/create : Tạo Album mới (Dòng 77 là dòng đầu tiên của phương thức này)
     */
    @PostMapping("/albums/create")
    public ResponseEntity<AlbumDto> createAlbum(@RequestBody AlbumRequest request) {
        String currentUserId = "MOCK_USER_ID_FROM_TOKEN"; // Lấy từ Security Context
        
        // SỬA LỖI ĐÃ XONG: albumService đã được khai báo và inject
        AlbumDto newAlbum = albumService.createPendingAlbum(request, currentUserId);
        
        return new ResponseEntity<>(newAlbum, HttpStatus.CREATED);
    }

    /**
     * GET /api/artist/albums/my-uploads : Xem tất cả Album đã upload
     */
    @GetMapping("/albums/my-uploads")
    public ResponseEntity<List<AlbumDto>> getMyAlbums() {
        String currentUserId = "MOCK_USER_ID_FROM_TOKEN";
        // Cần phương thức trong AlbumService để lấy Album theo userId
        List<AlbumDto> myAlbums = albumService.getAlbumsByUploader(currentUserId); 
        return ResponseEntity.ok(myAlbums);
    }
}