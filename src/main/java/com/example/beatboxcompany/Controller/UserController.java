package com.example.beatboxcompany.Controller;

import com.example.beatboxcompany.Dto.CommentDto;
import com.example.beatboxcompany.Dto.PlaylistDto;
import com.example.beatboxcompany.Dto.UserDto;
import com.example.beatboxcompany.Request.CommentRequest;
import com.example.beatboxcompany.Request.PlaylistRequest; 

import com.example.beatboxcompany.Service.CommentService; 
import com.example.beatboxcompany.Service.PlaylistService; 
import com.example.beatboxcompany.Service.SongService; 
import com.example.beatboxcompany.Service.UserService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasAnyRole('USER', 'ARTIST', 'ADMIN')") // Tất cả API trong đây cần đăng nhập
public class UserController {

    private final UserService userService;
    private final PlaylistService playlistService; // Khai báo PlaylistService
    private final CommentService commentService; // Khai báo CommentService
    private final SongService songService; // Khai báo SongService

    // Constructor để Inject (tiêm) các Service
    public UserController(
            UserService userService, 
            PlaylistService playlistService, 
            CommentService commentService,
            SongService songService) {
        this.userService = userService;
        this.playlistService = playlistService;
        this.commentService = commentService;
        this.songService = songService;
    }

    // --- 1. Quản lý Hồ sơ & Tương tác ---

    /**
     * GET /api/user/profile : Lấy thông tin cá nhân của người dùng đang đăng nhập
     */
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getCurrentUserProfile() {
        // TODO: Lấy ID người dùng từ Security Context (Giả định)
        String currentUserId = "MOCK_USER_ID_FROM_SECURITY_CONTEXT"; 

        UserDto userDto = userService.getUserById(currentUserId);
        return ResponseEntity.ok(userDto);
    }

    /**
     * POST /api/user/songs/{songId}/like : Thích/Bỏ thích bài hát
     */
    @PostMapping("/songs/{songId}/like")
    public ResponseEntity<Void> toggleLikeSong(@PathVariable String songId) {
        String currentUserId = "MOCK_USER_ID_FROM_SECURITY_CONTEXT";
        // TODO: Cần triển khai phương thức này trong UserService
        // userService.toggleLike(currentUserId, songId); 
        return ResponseEntity.ok().build();
    }

    // --- 2. Quản lý Playlist ---

    /**
     * POST /api/user/playlists : Tạo Playlist mới
     */
    @PostMapping("/playlists")
    public ResponseEntity<PlaylistDto> createPlaylist(@RequestBody PlaylistRequest request) {
        String currentUserId = "MOCK_USER_ID_FROM_SECURITY_CONTEXT"; 

        // Sử dụng biến instance playlistService đã inject
        PlaylistDto newPlaylist = playlistService.createPlaylist(request, currentUserId);
        return new ResponseEntity<>(newPlaylist, HttpStatus.CREATED);
    }

    /**
     * GET /api/user/playlists : Lấy tất cả Playlist của người dùng
     */
    @GetMapping("/playlists")
    public ResponseEntity<List<PlaylistDto>> getUserPlaylists() {
        String currentUserId = "MOCK_USER_ID_FROM_SECURITY_CONTEXT";
        // Sử dụng biến instance playlistService
        List<PlaylistDto> playlists = playlistService.getUserPlaylists(currentUserId);
        return ResponseEntity.ok(playlists);
    }

    /**
     * POST /api/user/playlists/{playlistId}/tracks/{trackId} : Thêm bài hát vào Playlist
     */
    @PostMapping("/playlists/{playlistId}/tracks/{trackId}")
    public ResponseEntity<PlaylistDto> addTrack(@PathVariable String playlistId, @PathVariable String trackId) {
        String currentUserId = "MOCK_USER_ID_FROM_SECURITY_CONTEXT";
        // SỬA LỖI: Gọi qua biến instance playlistService
        PlaylistDto updatedPlaylist = playlistService.addTrackToPlaylist(playlistId, trackId, currentUserId);
        return ResponseEntity.ok(updatedPlaylist);
    }

    /**
     * DELETE /api/user/playlists/{playlistId} : Xóa Playlist
     */
    @DeleteMapping("/playlists/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable String playlistId) {
        String currentUserId = "MOCK_USER_ID_FROM_SECURITY_CONTEXT";
        // Sử dụng biến instance playlistService
        playlistService.deletePlaylist(playlistId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    // --- 3. Quản lý Bình luận ---

    /**
     * POST /api/user/songs/{songId}/comments : Tạo bình luận mới
     */
    @PostMapping("/songs/{songId}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable String songId,
            @RequestBody CommentRequest request) {

        String currentUserId = "MOCK_USER_ID_FROM_SECURITY_CONTEXT"; 
        // Sử dụng biến instance commentService đã inject
        CommentDto newComment = commentService.createComment(songId, request, currentUserId);

        return new ResponseEntity<>(newComment, HttpStatus.CREATED);
    }

    /**
     * DELETE /api/user/comments/{commentId} : Xóa bình luận của chính mình
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteMyComment(@PathVariable String commentId) {
        String currentUserId = "MOCK_USER_ID_FROM_SECURITY_CONTEXT";
        // Sử dụng biến instance commentService đã inject
        commentService.deleteComment(commentId, currentUserId);

        return ResponseEntity.noContent().build();
    }
}