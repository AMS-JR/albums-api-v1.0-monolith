package com.afrosofttech.rest_jwt_demo.repository;

import com.afrosofttech.rest_jwt_demo.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByAlbumId(Long albumId);
}
