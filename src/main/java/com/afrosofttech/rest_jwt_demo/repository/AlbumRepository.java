package com.afrosofttech.rest_jwt_demo.repository;

import com.afrosofttech.rest_jwt_demo.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByAccount_Id(Long accountId);
}
