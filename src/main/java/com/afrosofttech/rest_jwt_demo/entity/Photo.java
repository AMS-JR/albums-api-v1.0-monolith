package com.afrosofttech.rest_jwt_demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private String description;
    private String originalFileName;
    private String fileName;
    @ManyToOne
    @JoinColumn(name = "album_id", referencedColumnName = "id", nullable = false)
    private Album album;
}
