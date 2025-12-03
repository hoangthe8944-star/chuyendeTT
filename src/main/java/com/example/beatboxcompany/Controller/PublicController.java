package com.example.beatboxcompany.Controller;

import com.example.beatboxcompany.Dto.AlbumDto;
import com.example.beatboxcompany.Dto.CommentDto;
import com.example.beatboxcompany.Dto.PlaylistDto;
import com.example.beatboxcompany.Dto.SongDto;
import com.example.beatboxcompany.Service.AlbumService;
import com.example.beatboxcompany.Service.CommentService;
import com.example.beatboxcompany.Service.PlaylistService;
import com.example.beatboxcompany.Service.SongService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    // Khai báo Dependency Injection cho tất cả các Service công khai
    private final SongService songService;
    private final AlbumService albumService;
    private final PlaylistService playlistService;
    private final CommentService commentService;
    // TODO: Có thể thêm ArtistService cho các API xem hồ sơ Artist công khai

    // Constructor để Inject (tiêm) các Service
    public PublicController(
            SongService songService, 
            AlbumService albumService,
            PlaylistService playlistService,
            CommentService commentService) {
        this.songService = songService;
        this.albumService = albumService;
        this.playlistService = playlistService;
        this.commentService = commentService;
    }

    // --- 1. Tìm kiếm và Trending ---
    
    /**
     * GET /api/public/search : Tìm kiếm toàn hệ thống (Song, Artist, Album)
     * Quyền hạn: Bất kỳ ai
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String query) {
        // TODO: Logic gọi từng Service (Song, Artist, Album) để tổng hợp kết quả tìm kiếm
        return ResponseEntity.ok(null);
    }

    /**
     * GET /api/public/songs/trending : Lấy danh sách bài hát hot (Top 10 mặc định)
     * Quyền hạn: Bất kỳ ai
     */
    @GetMapping("/songs/trending")
    public ResponseEntity<List<SongDto>> getTrendingSongs(@RequestParam(defaultValue = "10") int limit) {
        List<SongDto> trending = songService.getTrendingPublishedSongs(limit);
        return ResponseEntity.ok(trending);
    }
    
    // --- 2. Truy cập Album, Playlist Công khai ---

    /**
     * GET /api/public/albums/{albumId} : Xem chi tiết Album
     */
    @GetMapping("/albums/{albumId}")
    public ResponseEntity<AlbumDto> getAlbumDetails(@PathVariable String albumId) {
        // CHỈ lấy các Album đã PUBLISHED
        AlbumDto album = albumService.getPublishedAlbumById(albumId); 
        return ResponseEntity.ok(album);
    }

    /**
     * GET /api/public/playlists/{playlistId} : Xem chi tiết Playlist công khai
     */
    @GetMapping("/playlists/{playlistId}")
    public ResponseEntity<PlaylistDto> getPublicPlaylist(@PathVariable String playlistId) {
        // CHỈ cho phép xem nếu isPublic = true
        PlaylistDto playlist = playlistService.getPublicPlaylistDetails(playlistId);
        return ResponseEntity.ok(playlist);
    }

    // --- 3. Streaming và Tương tác ---

    /**
     * GET /api/public/songs/{songId}/stream
     * Mục đích: Lấy chi tiết bài hát và **tăng lượt nghe** (viewCount)
     */
    @GetMapping("/songs/{songId}/stream")
    public ResponseEntity<SongDto> streamSong(@PathVariable String songId) {
        // 1. Lấy bài hát (đảm bảo PUBLISHED)
        SongDto song = songService.getPublishedSongById(songId);

        // 2. Tăng View Count 
        songService.incrementViewCount(songId);

        return ResponseEntity.ok(song);
    }

    /**
     * GET /api/public/songs/{songId}/comments : Xem tất cả bình luận
     */
    @GetMapping("/songs/{songId}/comments")
    public ResponseEntity<List<CommentDto>> getSongComments(@PathVariable String songId) {
        // *Đảm bảo bài hát đã PUBLISHED trước khi gọi CommentService*
        // Việc này đã được đảm bảo gián tiếp bằng cách gọi songService.getPublishedSongById() 
        // trong phương thức streamSong, nhưng để API này độc lập, cần kiểm tra lại:
        songService.getPublishedSongById(songId); 

        List<CommentDto> comments = commentService.getCommentsBySongId(songId);
        return ResponseEntity.ok(comments);
    }
}