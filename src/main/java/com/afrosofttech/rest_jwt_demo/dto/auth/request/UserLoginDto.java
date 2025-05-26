package com.afrosofttech.rest_jwt_demo.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginDto {
    @Email
    @Schema(description = "Email address", example = "user1@user.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @Size(min = 6, max = 20)
    @Schema(description = "Password", example = "password1",
            requiredMode = Schema.RequiredMode.REQUIRED, minLength = 6, maxLength = 20)
    private String password;
}
