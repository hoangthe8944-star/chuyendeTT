package com.example.beatboxcompany.Service.Impl;

import com.example.beatboxcompany.Dto.AlbumDto;
import com.example.beatboxcompany.Entity.Album;
import com.example.beatboxcompany.Entity.Song;
import com.example.beatboxcompany.Entity.Artist;
import com.example.beatboxcompany.Mapper.AlbumMapper;
import com.example.beatboxcompany.Repository.AlbumRepository;
import com.example.beatboxcompany.Repository.ArtistRepository;
import com.example.beatboxcompany.Repository.SongRepository;
import com.example.beatboxcompany.Request.AlbumRequest;
import com.example.beatboxcompany.Request.TrackRequest;
import com.example.beatboxcompany.Service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SongRepository songRepository;
    private final AlbumMapper albumMapper;

    @Override
    public AlbumDto createPendingAlbum(AlbumRequest request, String currentUserId) {
        Artist artist = artistRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Artist!"));

        if (!artist.getId().equals(request.getArtistId())) {
            throw new SecurityException("Không được tạo album cho artist khác!");
        }

        Album album = new Album();
        album.setId(UUID.randomUUID().toString());
        album.setTitle(request.getTitle());
        album.setReleaseDate(request.getReleaseDate());
        album.setArtistId(artist.getId());
        album.setStatus("PENDING");
        album.setCreatedAt(LocalDateTime.now());
        album.setUpdatedAt(LocalDateTime.now());
        album.setTrackIds(new ArrayList<>()); // quan trọng: khởi tạo list

        long totalDuration = 0;
        int trackIndex = 1;

        for (TrackRequest tr : request.getTracks()) {
            int trackNo = (tr.getTrackNumber() != null && tr.getTrackNumber() > 0)
                    ? tr.getTrackNumber()
                    : trackIndex;

            Song song = new Song();
            song.setId(UUID.randomUUID().toString());
            song.setTitle(tr.getTitle());
            song.setArtistId(artist.getId());
            song.setDurationMs((long) tr.getDurationMs());
            song.setIsExplicit(tr.getIsExplicit() != null && tr.getIsExplicit());
            song.setStatus("PENDING");

            songRepository.save(song); // lưu song trước

            // Thêm thẳng songId vào album (MongoDB không cần bảng trung gian)
            album.getTrackIds().add(song.getId());

            totalDuration += tr.getDurationMs();
            trackIndex++;
        }

        album.setTotalTracks(request.getTracks().size());
        album.setTotalDurationMs(totalDuration);

        albumRepository.save(album); // lưu album cuối cùng

        return albumMapper.toFullDto(album);
    }

    @Override
    public List<AlbumDto> getAlbumsByStatus(String status) {
        return albumRepository.findByStatus(status).stream()
                .map(albumMapper::toFullDto)
                .toList();
    }

    @Override
    public AlbumDto approveAlbum(String albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album không tồn tại!"));

        album.setStatus("PUBLISHED");
        album.setUpdatedAt(LocalDateTime.now());
        albumRepository.save(album);

        // Duyệt các song trong album và publish luôn
        for (String songId : album.getTrackIds()) {
            songRepository.findById(songId).ifPresent(song -> {
                song.setStatus("PUBLISHED");
                songRepository.save(song);
            });
        }

        return albumMapper.toFullDto(album);
    }

    @Override
    public AlbumDto rejectAlbum(String albumId, String reason) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album không tồn tại!"));
        album.setStatus("REJECTED");
        album.setRejectedReason(reason);
        album.setUpdatedAt(LocalDateTime.now());
        albumRepository.save(album);
        return albumMapper.toFullDto(album);
    }

    @Override
    public AlbumDto getPublishedAlbumById(String albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album không tồn tại!"));
        if (!"PUBLISHED".equals(album.getStatus())) {
            throw new SecurityException("Album chưa được phát hành!");
        }
        return albumMapper.toFullDto(album);
    }

    @Override
    public List<AlbumDto> getAlbumsByUploader(String currentUserId) {
        String artistId = artistRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new RuntimeException("Không phải artist!"))
                .getId();

        return albumRepository.findByArtistId(artistId).stream()
                .map(albumMapper::toFullDto)
                .toList();
    }

    @Override
    public AlbumDto updateAlbumStatus(String albumId, String status, String reason) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album không tồn tại!"));

        album.setStatus(status);
        if ("REJECTED".equals(status)) {
            album.setRejectedReason(reason);
        } else {
            album.setRejectedReason(null);
        }
        album.setUpdatedAt(LocalDateTime.now());
        albumRepository.save(album);

        // Nếu approve → publish luôn các song
        if ("PUBLISHED".equals(status)) {
            if (album.getTrackIds() != null) {
                album.getTrackIds().forEach(songId -> songRepository.findById(songId).ifPresent(song -> {
                    song.setStatus("PUBLISHED");
                    songRepository.save(song);
                }));
            }
        }

        return albumMapper.toFullDto(album);
    }
}