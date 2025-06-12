package com.afrosofttech.rest_jwt_demo.mapper.album;

import com.afrosofttech.rest_jwt_demo.dto.album.request.AlbumRequestDto;
import com.afrosofttech.rest_jwt_demo.dto.album.response.AlbumResponseDto;
import com.afrosofttech.rest_jwt_demo.dto.photo.response.PhotoResponseDto;
import com.afrosofttech.rest_jwt_demo.entity.Album;

import java.util.List;

public class AlbumMapper {
    public static AlbumResponseDto toDto(Album entity, List<PhotoResponseDto> photos) {
        return AlbumResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .photos(photos)
                .build();
    }

    public static Album toEntity(AlbumRequestDto dto) {
        return null;
    }
}
