package com.afrosofttech.rest_jwt_demo.dto.auth.response;

import lombok.Builder;

@Builder
public record AccountResponseDto(Long id, String email, String authorities) {}

