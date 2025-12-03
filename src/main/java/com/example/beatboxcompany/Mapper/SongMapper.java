package com.example.beatboxcompany.Mapper;

import com.example.beatboxcompany.Dto.SongDto;
import com.example.beatboxcompany.Entity.Song;
import com.example.beatboxcompany.Request.SongUploadRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class SongMapper {

    public Song toEntity(SongUploadRequest request, String artistId) {
        Song song = new Song();
        song.setTitle(request.getTitle());
        song.setArtistId(artistId);
        song.setAlbumId(request.getAlbumId());
        song.setDuration(request.getDuration());
        song.setStreamUrl(request.getStreamUrl());
        song.setCoverUrl(request.getCoverUrl());
        song.setGenre(request.getGenre() != null ? request.getGenre() : Collections.emptyList());
        song.setIsExplicit(request.getIsExplicit() != null && request.getIsExplicit());
        song.setViewCount(0L);
        song.setStatus("PENDING");
        return song;
    }

    public SongDto toDto(Song song) {
        SongDto dto = new SongDto();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        dto.setDuration(song.getDuration());
        dto.setStreamUrl(song.getStreamUrl());
        dto.setCoverUrl(song.getCoverUrl());
        dto.setArtistId(song.getArtistId());
        dto.setAlbumId(song.getAlbumId());
        dto.setGenre(song.getGenre());
        dto.setViewCount(song.getViewCount());
        dto.setIsExplicit(song.getIsExplicit());
        dto.setStatus(song.getStatus());
        return dto;
    }
}