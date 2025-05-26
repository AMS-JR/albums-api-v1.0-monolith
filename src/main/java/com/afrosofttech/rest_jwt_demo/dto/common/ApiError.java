package com.afrosofttech.rest_jwt_demo.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiError {
    private String code; // e.g. VALIDATION_ERROR, NOT_FOUND
    private List<String> details;
}
