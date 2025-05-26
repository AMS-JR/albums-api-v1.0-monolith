package com.afrosofttech.rest_jwt_demo.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorityDto {
    @NotBlank
    @Schema(description = "authorities", example = "USER",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String authorities;
}
