package com.afrosofttech.rest_jwt_demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private String description;
    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName ="id", nullable = false)
    private Account account;
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    private List<Photo> photos;
}
