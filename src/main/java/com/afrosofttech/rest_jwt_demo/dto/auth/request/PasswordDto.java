package com.afrosofttech.rest_jwt_demo.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordDto {
    @Size(min = 6, max = 20)
    @Schema(description = "Password", example = "Password",
            requiredMode = Schema.RequiredMode.REQUIRED, minLength = 6, maxLength = 20)
    private String password;
}
