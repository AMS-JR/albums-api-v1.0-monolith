package com.afrosofttech.rest_jwt_demo.dto.album.response;

import com.afrosofttech.rest_jwt_demo.dto.photo.request.PhotoDto;
import com.afrosofttech.rest_jwt_demo.dto.photo.response.PhotoPayload;
import lombok.Builder;

import java.util.List;

@Builder
public record AlbumPayload(Long id, String name, String description, List<PhotoPayload> photos) {}
