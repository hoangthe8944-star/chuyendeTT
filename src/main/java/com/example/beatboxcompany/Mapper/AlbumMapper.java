package com.example.beatboxcompany.Mapper;

import com.example.beatboxcompany.Dto.AlbumDto;
import com.example.beatboxcompany.Dto.TrackDto;
import com.example.beatboxcompany.Entity.Album;
import com.example.beatboxcompany.Entity.Song;
import com.example.beatboxcompany.Repository.ArtistRepository;
import com.example.beatboxcompany.Repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AlbumMapper {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private SongRepository songRepository;

    // Mapper chính – dùng trong service
    public AlbumDto toFullDto(Album album) {
        AlbumDto dto = new AlbumDto();
        dto.setId(album.getId());
        dto.setTitle(album.getTitle());
        dto.setReleaseDate(album.getReleaseDate());
        dto.setCoverUrl(album.getCoverUrl());
        dto.setArtistId(album.getArtistId());
        dto.setStatus(album.getStatus());
        dto.setRejectedReason(album.getRejectedReason());
        dto.setTotalTracks(album.getTotalTracks());
        dto.setTotalDurationMs(album.getTotalDurationMs());

        // Lấy tên artist
        artistRepository.findById(album.getArtistId())
                .ifPresent(artist -> dto.setArtistName(artist.getName()));

        // Lấy danh sách tracks từ trackIds trong album (MongoDB style)
        if (album.getTrackIds() != null && !album.getTrackIds().isEmpty()) {
            var tracks = album.getTrackIds().stream()
                .map(songId -> songRepository.findById(songId).orElse(null))
                .filter(song -> song != null)
                .map(song -> {
                    TrackDto trackDto = new TrackDto();
                    trackDto.setSongId(song.getId());
                    trackDto.setTitle(song.getTitle());
                    trackDto.setDurationMs(song.getDurationMs());
                    trackDto.setIsExplicit(song.getIsExplicit() != null && song.getIsExplicit());
                    // Nếu cần trackNumber sau này thì thêm trường vào Album hoặc sort theo thứ tự thêm
                    return trackDto;
                })
                .collect(Collectors.toList());

            dto.setTracks(tracks);
        }

        return dto;
    }

    // Mapper đơn giản (không cần load tracks chi tiết)
    public AlbumDto toSimpleDto(Album album) {
        AlbumDto dto = new AlbumDto();
        dto.setId(album.getId());
        dto.setTitle(album.getTitle());
        dto.setReleaseDate(album.getReleaseDate());
        dto.setCoverUrl(album.getCoverUrl());
        dto.setArtistId(album.getArtistId());
        dto.setStatus(album.getStatus());
        dto.setTotalTracks(album.getTotalTracks());
        dto.setTotalDurationMs(album.getTotalDurationMs());

        artistRepository.findById(album.getArtistId())
                .ifPresent(artist -> dto.setArtistName(artist.getName()));

        return dto;
    }
}