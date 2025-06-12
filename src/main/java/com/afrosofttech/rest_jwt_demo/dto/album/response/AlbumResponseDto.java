package com.afrosofttech.rest_jwt_demo.dto.album.response;

import com.afrosofttech.rest_jwt_demo.dto.photo.response.PhotoResponseDto;
import lombok.Builder;

import java.util.List;

@Builder
public record AlbumResponseDto(Long id, String name, String description, List<PhotoResponseDto> photos) {}
