package com.afrosofttech.rest_jwt_demo.dto.album.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumDto {
    @NotBlank
    @Schema(description = "Album name", example = "name",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @NotBlank
    @Schema(description = "Description of the album", example = "Some description",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;
}
