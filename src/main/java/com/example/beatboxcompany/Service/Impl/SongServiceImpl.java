package com.example.beatboxcompany.Service.Impl;

import com.example.beatboxcompany.Entity.Song;
import com.example.beatboxcompany.Dto.SongDto;
import com.example.beatboxcompany.Request.SongUploadRequest;
import com.example.beatboxcompany.Mapper.SongMapper;
import com.example.beatboxcompany.Repository.SongRepository;
import com.example.beatboxcompany.Repository.ArtistRepository;
import com.example.beatboxcompany.Service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final SongMapper songMapper; // THÊM DÒNG NÀY

    @Override
    public SongDto createPendingSong(SongUploadRequest request, String currentUserId) {
        String artistId = artistRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("Bạn không phải Artist!"))
                .getId();

        if (!artistId.equals(request.getArtistId())) {
            throw new SecurityException("Không có quyền tạo bài hát cho Artist khác.");
        }

        Song song = songMapper.toEntity(request, artistId); // SỬA DÒNG NÀY
        Song savedSong = songRepository.save(song);

        return songMapper.toDto(savedSong); // SỬA DÒNG NÀY
    }

    @Override
    public SongDto updateStatus(String songId, String newStatus) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài hát!"));

        if (!List.of("PUBLISHED", "REJECTED").contains(newStatus)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        }

        song.setStatus(newStatus);
        Song updatedSong = songRepository.save(song);
        return songMapper.toDto(updatedSong); // SỬA DÒNG NÀY
    }

    @Override
    public List<SongDto> getSongsByStatus(String status) {
        return songRepository.findByStatus(status).stream()
                .map(songMapper::toDto) // SỬA DÒNG NÀY
                .collect(Collectors.toList());
    }

    @Override
    public List<SongDto> getSongsByUploader(String currentUserId) {
        String artistId = artistRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("Bạn không phải Artist để xem bài hát đã upload!"))
                .getId();

        return songRepository.findByArtistId(artistId).stream()
                .map(songMapper::toDto) // SỬA DÒNG NÀY
                .collect(Collectors.toList());
    }

    @Override
    public SongDto getPublishedSongById(String songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài hát!"));

        if (!"PUBLISHED".equals(song.getStatus())) {
            throw new SecurityException("Bài hát chưa được phát hành.");
        }
        return songMapper.toDto(song); // SỬA DÒNG NÀY
    }

    @Override
    public SongDto incrementViewCount(String songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Bài hát không tồn tại!"));

        if (!"PUBLISHED".equals(song.getStatus())) {
            throw new SecurityException("Không thể tăng view count cho bài chưa phát hành.");
        }

        song.setViewCount(song.getViewCount() + 1);
        Song updatedSong = songRepository.save(song);
        return songMapper.toDto(updatedSong);
    }

    @Override
    public List<SongDto> getTrendingPublishedSongs(int limit) {
        return songRepository.findByStatusOrderByViewCountDesc("PUBLISHED").stream()
                .limit(limit)
                .map(songMapper::toDto) // SỬA DÒNG NÀY
                .collect(Collectors.toList());
    }
}