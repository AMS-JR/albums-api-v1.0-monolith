package com.afrosofttech.rest_jwt_demo.mapper.photo;

import com.afrosofttech.rest_jwt_demo.dto.photo.request.PhotoRequestDto;
import com.afrosofttech.rest_jwt_demo.dto.photo.response.PhotoResponseDto;
import com.afrosofttech.rest_jwt_demo.entity.Photo;

public class PhotoMapper {

    public static PhotoResponseDto toDto(Photo entity, String link) {
        return  PhotoResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .fileName(entity.getFileName())
                .downloadLink(link)
                .build();
    }

    public static Photo toEntity(PhotoRequestDto dto) {
        return null;
    }
}
