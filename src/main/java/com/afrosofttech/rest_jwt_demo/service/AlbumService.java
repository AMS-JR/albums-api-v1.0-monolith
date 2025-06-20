package com.afrosofttech.rest_jwt_demo.service;

import com.afrosofttech.rest_jwt_demo.dto.album.request.AlbumRequestDto;
import com.afrosofttech.rest_jwt_demo.dto.album.response.AlbumResponseDto;
import com.afrosofttech.rest_jwt_demo.dto.photo.request.PhotoRequestDto;
import com.afrosofttech.rest_jwt_demo.dto.photo.response.PhotoResponseDto;
import com.afrosofttech.rest_jwt_demo.entity.Account;
import com.afrosofttech.rest_jwt_demo.entity.Album;
import com.afrosofttech.rest_jwt_demo.entity.Photo;
import com.afrosofttech.rest_jwt_demo.exception.ResourceNotFoundException;
import com.afrosofttech.rest_jwt_demo.exception.UnauthorizedAccessException;
import com.afrosofttech.rest_jwt_demo.mapper.album.AlbumMapper;
import com.afrosofttech.rest_jwt_demo.mapper.photo.PhotoMapper;
import com.afrosofttech.rest_jwt_demo.repository.AccountRepository;
import com.afrosofttech.rest_jwt_demo.repository.AlbumRepository;
import com.afrosofttech.rest_jwt_demo.repository.PhotoRepository;
import com.afrosofttech.rest_jwt_demo.util.AppUtil;
import com.afrosofttech.rest_jwt_demo.util.constants.FileType;
import com.afrosofttech.rest_jwt_demo.util.constants.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final AccountRepository accountRepository;
    private final PhotoRepository photoRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository,
                        AccountRepository accountRepository,
                        PhotoRepository photoRepository) {
        this.albumRepository = albumRepository;
        this.accountRepository = accountRepository;
        this.photoRepository = photoRepository;
    }
    public List<AlbumResponseDto> index(){
        Long accountId = this.getCurrentLoggedInUser();
        List<AlbumResponseDto> albums = new ArrayList<>();

        this.findAllByAccountId(accountId).forEach(album -> {

            List<PhotoResponseDto> photos = new ArrayList<>();
            for(Photo photo : photoRepository.findByAlbumId(album.getId())){
                String link = "/albums/"+album.getId()+"/photos/"+photo.getId()+"/download";
                photos.add(PhotoMapper.toDto(photo, link));
            }
            AlbumResponseDto albumResponseDto = AlbumMapper.toDto(album, photos);

            albums.add(albumResponseDto);
        });
        return albums;
    }
    public AlbumResponseDto getAlbumById(Long albumId){
        Long accountId = this.getCurrentLoggedInUser();

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND.format(albumId)));
        // Access check
        if (!accountId.equals(album.getAccount().getId())) {
            throw new UnauthorizedAccessException(ErrorMessage.UNAUTHORIZED_ALBUM_ACCESS.format(albumId));
        }

        List<PhotoResponseDto> photos = new ArrayList<>();
        for (Photo photo : photoRepository.findByAlbumId(albumId)) {
            String link = "/albums/" + album.getId() + "/photos/" + photo.getId() + "/download";
            photos.add(PhotoMapper.toDto(photo, link));
        }

        return AlbumMapper.toDto(album, photos);
    }
    public AlbumResponseDto create(AlbumRequestDto albumRequestDto, String email) throws ResourceNotFoundException {
        Album album = new Album();
        album.setName(albumRequestDto.getName());
        album.setDescription(albumRequestDto.getDescription());

        Account account = accountRepository.findByEmail(email)
                     .orElseThrow(() -> new ResourceNotFoundException("Failed to add album." +
                "Account not found for email: " + email));

        album.setAccount(account);

        Album savedAlbum = albumRepository.save(album);

        return new AlbumResponseDto(savedAlbum.getId(), savedAlbum.getName(), savedAlbum.getDescription(), null);
    }

    public AlbumResponseDto read(){
        return null;
    }
    public AlbumResponseDto updateAlbum(Long albumId, AlbumRequestDto albumRequestDto){
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found"));

        Long accountId = this.getCurrentLoggedInUser();
        // Access check
        if (!accountId.equals(album.getAccount().getId())) {
            throw new UnauthorizedAccessException(ErrorMessage.UNAUTHORIZED_ALBUM_ACCESS.format(albumId));
        }

        // Update album details
        album.setName(albumRequestDto.getName());
        album.setDescription(albumRequestDto.getDescription());

        // Save the updated album
        albumRepository.save(album);

        // Convert to AlbumPayload
        AlbumResponseDto albumResponseDto = AlbumResponseDto.builder()
                .id(album.getId())
                .name(album.getName())
                .description(album.getDescription())
                .photos(album.getPhotos().stream()
                        .map(photo -> PhotoResponseDto.builder()
                                .id(photo.getId())
                                .name(photo.getName())
                                .description(photo.getDescription())
                                .fileName(photo.getFileName())
                                .downloadLink("/albums/" + album.getId() + "/photos/" + photo.getId() + "/download")
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return albumResponseDto;
    }
    public Map<String,String> deleteAlbum(Long albumId){
        Long accountId = this.getCurrentLoggedInUser();
        Album album = albumRepository.findById(albumId).get();
        if (!album.getAccount().getId().equals(accountId)) {
            throw new UnauthorizedAccessException(ErrorMessage.UNAUTHORIZED_ALBUM_DELETE.format());
        }
        // Proceed with deletion
        List<Photo> photos = photoRepository.findByAlbumId(albumId);
        for (Photo photo : photos) {
            AppUtil.deleteFile(photo.getFileName(), FileType.PHOTO.getFolderName(), albumId);
            AppUtil.deleteFile(photo.getFileName(), FileType.THUMBNAIL.getFolderName(), albumId);
            photoRepository.deleteById(photo.getId());
        }
        albumRepository.deleteById(albumId);
        Map<String, String> response = new HashMap<>();
        response.put("success", "Album deleted successfully!");
        return response;
    }

    public Album save(Album album){
        return albumRepository.save(album);
    }
    private Long getCurrentLoggedInUser() {
        // Implementation to fetch the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return this.getAccountId(authentication);
    }
    private Long getAccountId(Authentication authentication) { // not secure enough
        // Extract account ID from authentication
        // Implementation depends on your security setup
        String email = authentication.getName();
        Account account = accountRepository.findByEmail(email).get();
        return account.getId();
    }
    private List<Album> findAllByAccountId(Long accountId) {
        return albumRepository.findByAccount_Id(accountId);
    }
    private Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }
    public Optional<Album> findById(Long id) {
        return albumRepository.findById(id);
    }
}
