package com.afrosofttech.rest_jwt_demo.dto.photo.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoRequestDto {
    private String name;
    private String description;
}
