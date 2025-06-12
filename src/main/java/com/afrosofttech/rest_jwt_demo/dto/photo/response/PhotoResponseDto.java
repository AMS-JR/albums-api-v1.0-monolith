package com.afrosofttech.rest_jwt_demo.dto.photo.response;

import lombok.Builder;

@Builder
public record PhotoResponseDto(Long id, String name, String description, String fileName, String downloadLink) {}
