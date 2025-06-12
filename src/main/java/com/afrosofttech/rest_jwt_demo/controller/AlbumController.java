package com.afrosofttech.rest_jwt_demo.controller;

import com.afrosofttech.rest_jwt_demo.dto.album.request.AlbumRequestDto;
import com.afrosofttech.rest_jwt_demo.dto.album.response.AlbumResponseDto;
import com.afrosofttech.rest_jwt_demo.dto.photo.request.PhotoRequestDto;
import com.afrosofttech.rest_jwt_demo.exception.ResourceNotFoundException;
import com.afrosofttech.rest_jwt_demo.service.AlbumService;
import com.afrosofttech.rest_jwt_demo.service.PhotoService;
import com.afrosofttech.rest_jwt_demo.util.constants.AlbumError;
import com.afrosofttech.rest_jwt_demo.util.constants.FileType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/albums")
@Tag(name = "Album Controller", description = "Controller for Album management")
@Slf4j
@SecurityRequirement(name = "afrosofttech-demo-api")
public class AlbumController {
    private final AlbumService albumService;
    private final PhotoService photoService;

    public AlbumController(AlbumService albumService, PhotoService photoService) {
        this.albumService = albumService;
        this.photoService = photoService;
    }

    @GetMapping(value = "/", produces ="application/json")
//    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @Operation(summary = "Show albums")
    public ResponseEntity<List<AlbumResponseDto>> getAllAlbums() {
        List<AlbumResponseDto> albums = albumService.index();
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Show an Album")
    public ResponseEntity<AlbumResponseDto> getAlbumById(@PathVariable Long id) {
        AlbumResponseDto albumResponseDto = albumService.getAlbumById(id);
        return ResponseEntity.ok(albumResponseDto);
    }
    @PostMapping(value= "/add", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an Album")
    public ResponseEntity<AlbumResponseDto> CreateAlbum(@Valid @RequestBody AlbumRequestDto albumRequestDto, Authentication authentication) {
        try {
            AlbumResponseDto albumResponseDto = albumService.create(albumRequestDto, authentication.getName());
            return new ResponseEntity<>(albumResponseDto, HttpStatus.CREATED);
        } catch (ResourceNotFoundException ex) {
            log.debug(AlbumError.ALBUM_ADD_ERROR.name(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
@PutMapping("/{id}")
@Operation(summary = "Update an Album")
public ResponseEntity<AlbumResponseDto> updateAlbum(@PathVariable Long id, @RequestBody AlbumRequestDto albumRequestDto) {
    AlbumResponseDto updatedAlbum = albumService.updateAlbum(id, albumRequestDto);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updatedAlbum);
}
@DeleteMapping("/{id}")
@Operation(summary = "Delete an album")
public ResponseEntity<?> deleteAlbum(@PathVariable Long id) {
    Map<String, String> response = albumService.deleteAlbum(id);
    return ResponseEntity.ok(response);
}
@DeleteMapping("/{id}/photos/{photoId}")
@Operation(summary = "Delete a album photo")
public ResponseEntity<?> deletePhoto(
        @PathVariable Long id,
        @PathVariable Long photoId) {
    Map<String, String> response = photoService.deletePhoto(id, photoId);
    return ResponseEntity.ok(response);
}

@PostMapping(value = "/{albumId}/photos", consumes = "multipart/form-data")
@Operation(summary = "Add photo to an album")
public ResponseEntity<Map<String, List<String>>> createPhotos(@PathVariable Long albumId,
                                                              @RequestPart("files") MultipartFile[] files,
                                                              Authentication authentication) {
    Map<String, List<String>> fileNames = photoService.savePhotos(albumId, files, authentication);
    return ResponseEntity.ok(fileNames);
}
@PutMapping("/{id}/photos/{photoId}")
@Operation(summary = "Update a photo in an album")
public ResponseEntity<Map<String, String>> updatePhoto(
        @PathVariable Long id,
        @PathVariable Long photoId,
        @RequestBody PhotoRequestDto photoRequestDto) {
    Map<String, String> response = photoService.updatePhoto(id, photoId, photoRequestDto);
    return ResponseEntity.ok(response);
}
@GetMapping("/{albumId}/photos/{photoId}/download")
@Operation(summary = "Download a photo")
public ResponseEntity<?> downloadPhoto(
        @PathVariable Long albumId,
        @PathVariable Long photoId) {

    Resource resource = photoService.downloadFile(albumId, photoId, FileType.PHOTO);
    // Prepare response
    String contentType = "application/octet-stream";
    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
}
@GetMapping("/{albumId}/thumbnails/{photoId}/download")
@Operation(summary = "Download a thumbnail")
public ResponseEntity<Resource> downloadThumbnail(
        @RequestParam Long albumId,
        @RequestParam Long photoId) {
    Resource resource = photoService.downloadFile(albumId, photoId, FileType.THUMBNAIL);
    // Prepare response
    String contentType = "application/octet-stream";
    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
}
}
