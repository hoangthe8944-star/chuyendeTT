package com.example.beatboxcompany.Controller;

import com.example.beatboxcompany.Dto.AlbumDto;
import com.example.beatboxcompany.Dto.ArtistDto;
import com.example.beatboxcompany.Dto.SongDto;
import com.example.beatboxcompany.Dto.UserDto;
import com.example.beatboxcompany.Dto.SubscriptionDto; // Import SubscriptionDto
import com.example.beatboxcompany.Service.AlbumService;
import com.example.beatboxcompany.Service.ArtistService;
// import com.example.beatboxcompany.Service.CommentService; // Loại bỏ CommentService
import com.example.beatboxcompany.Service.SongService;
import com.example.beatboxcompany.Service.UserService;
import com.example.beatboxcompany.Service.SubscriptionService; // Import SubscriptionService

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Bảo vệ toàn bộ Controller này
public class AdminController {

    private final SongService songService;
    private final UserService userService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final SubscriptionService subscriptionService; // Khai báo SubscriptionService

    // Khai báo đầy đủ các Service trong Constructor
    public AdminController(
            SongService songService,
            UserService userService,
            ArtistService artistService,
            AlbumService albumService,
            SubscriptionService subscriptionService) { // Thêm SubscriptionService
        this.songService = songService;
        this.userService = userService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.subscriptionService = subscriptionService; // Gán giá trị
    }

    // -----------------------------------------------------
    // --- Quản lý Người dùng (Users) ---
    // -----------------------------------------------------

    /**
     * GET /api/admin/users : Lấy danh sách tất cả người dùng
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * DELETE /api/admin/users/{userId} : Xóa người dùng
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // -----------------------------------------------------
    // --- Quản lý Gói Thành viên (Subscriptions) ---
    // -----------------------------------------------------

    /**
     * GET /api/admin/subscriptions : Lấy danh sách tất cả các gói thành viên
     */
    @GetMapping("/subscriptions")
    public ResponseEntity<List<SubscriptionDto>> getAllSubscriptions() {
        // Giả định SubscriptionService có phương thức getAllSubscriptions()
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    /**
     * POST /api/admin/subscriptions : Tạo gói thành viên mới
     */
    @PostMapping("/subscriptions")
    public ResponseEntity<SubscriptionDto> createSubscription(@RequestBody SubscriptionDto subscriptionDto) {
        // Giả định SubscriptionService có phương thức createSubscription()
        SubscriptionDto newSubscription = subscriptionService.createSubscription(subscriptionDto);
        return ResponseEntity.ok(newSubscription);
    }

    /**
     * DELETE /api/admin/subscriptions/{subId} : Xóa gói thành viên
     */
    @DeleteMapping("/subscriptions/{subId}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable String subId) {
        // Giả định SubscriptionService có phương thức deleteSubscription()
        subscriptionService.deleteSubscription(subId);
        return ResponseEntity.noContent().build();
    }

    // -----------------------------------------------------
    // --- Quản lý Bài hát (Songs) & Kiểm duyệt ---
    // -----------------------------------------------------

    /**
     * GET /api/admin/songs/pending : Lấy danh sách bài hát chờ duyệt
     */
    @GetMapping("/songs/pending")
    public ResponseEntity<List<SongDto>> getPendingSongs() {
        return ResponseEntity.ok(songService.getSongsByStatus("PENDING"));
    }

    /**
     * PUT /api/admin/songs/{songId}/approve : Phê duyệt bài hát
     */
    @PutMapping("/songs/{songId}/approve")
    public ResponseEntity<SongDto> approveSong(@PathVariable String songId) {
        SongDto approvedSong = songService.updateStatus(songId, "PUBLISHED");
        return ResponseEntity.ok(approvedSong);
    }

    // -----------------------------------------------------
    // --- Quản lý Album (Albums) & Kiểm duyệt ---
    // -----------------------------------------------------

    /**
     * GET /api/admin/albums/pending : Lấy danh sách Album chờ duyệt
     */
    @GetMapping("/albums/pending")
    public ResponseEntity<List<AlbumDto>> getPendingAlbums() {
        return ResponseEntity.ok(albumService.getAlbumsByStatus("PENDING"));
    }

    /**
     * PUT /api/admin/albums/{albumId}/status : Duyệt/Từ chối Album
     */
    @PutMapping("/albums/{albumId}/status")
    public ResponseEntity<AlbumDto> updateAlbumStatus(
            @PathVariable String albumId,
            @RequestParam String status,
            @RequestParam(required = false) String reason) {

        AlbumDto updatedAlbum = albumService.updateAlbumStatus(albumId, status, reason);
        return ResponseEntity.ok(updatedAlbum);
    }
    // -----------------------------------------------------
    // --- Quản lý Nghệ sĩ (Artists) ---
    // -----------------------------------------------------

    /**
     * GET /api/admin/artists : Lấy danh sách tất cả hồ sơ Nghệ sĩ
     */
    @GetMapping("/artists")
    public ResponseEntity<List<ArtistDto>> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    /**
     * PUT /api/admin/artists/{artistId}/name : Sửa tên nghệ sĩ (nếu cần can thiệp)
     */
    @PutMapping("/artists/{artistId}/name")
    public ResponseEntity<ArtistDto> updateArtistName(@PathVariable String artistId, @RequestParam String newName) {
        ArtistDto updatedArtist = artistService.adminUpdateName(artistId, newName);
        return ResponseEntity.ok(updatedArtist);
    }

    @PostMapping("/artists/create")
    public ResponseEntity<?> createArtistByAdmin(@RequestBody CreateArtistRequest request) {
        try {
            ArtistDto artist = artistService.createArtistByAdmin(
                    request.userId(),
                    request.artistName());
            return ResponseEntity.ok(artist);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    record CreateArtistRequest(String userId, String artistName) {
    }
}