package com.afrosofttech.rest_jwt_demo.service;

import com.afrosofttech.rest_jwt_demo.dto.album.request.AlbumDto;
import com.afrosofttech.rest_jwt_demo.dto.album.response.AlbumPayload;
import com.afrosofttech.rest_jwt_demo.dto.photo.request.PhotoDto;
import com.afrosofttech.rest_jwt_demo.dto.photo.response.PhotoPayload;
import com.afrosofttech.rest_jwt_demo.entity.Account;
import com.afrosofttech.rest_jwt_demo.entity.Album;
import com.afrosofttech.rest_jwt_demo.entity.Photo;
import com.afrosofttech.rest_jwt_demo.exception.ResourceNotFoundException;
import com.afrosofttech.rest_jwt_demo.exception.UnauthorizedAccessException;
import com.afrosofttech.rest_jwt_demo.repository.AccountRepository;
import com.afrosofttech.rest_jwt_demo.repository.AlbumRepository;
import com.afrosofttech.rest_jwt_demo.repository.PhotoRepository;
import com.afrosofttech.rest_jwt_demo.util.AppUtil;
import com.afrosofttech.rest_jwt_demo.util.constants.FileType;
import com.afrosofttech.rest_jwt_demo.util.constants.exceptions.ErrorMessage;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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
    public AlbumService(AlbumRepository albumRepository, AccountRepository accountRepository, PhotoRepository photoRepository) {
        this.albumRepository = albumRepository;
        this.accountRepository = accountRepository;
        this.photoRepository = photoRepository;
    }
    public List<AlbumPayload> index(){
        Long accountId = this.getCurrentLoggedInUser();
        List<AlbumPayload> albums = new ArrayList<>();

        this.findAllByAccountId(accountId).forEach(album -> {

            List<PhotoPayload> photos = new ArrayList<>();
            for(Photo photo : photoRepository.findByAlbumId(album.getId())){
                String link = "/albums/"+album.getId()+"/photos/"+photo.getId()+"/download";
                photos.add(
                        PhotoPayload.builder()
                                .id(photo.getId())
                                .name(photo.getName())
                                .description(photo.getDescription())
                                .fileName(photo.getFileName())
                                .downloadLink(link)
                                .build()
                );
            }
            AlbumPayload albumPayload = AlbumPayload.builder()
                    .id(album.getId())
                    .name(album.getName())
                    .description(album.getDescription())
                    .photos(photos)
                    .build();

            albums.add(albumPayload);
        });
        return albums;
    }
    public AlbumPayload getAlbumById(Long albumId){
        Long accountId = this.getCurrentLoggedInUser();

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND.format(albumId)));
        // Access check
        if (!accountId.equals(album.getAccount().getId())) {
            throw new UnauthorizedAccessException(ErrorMessage.UNAUTHORIZED_ALBUM_ACCESS.format(albumId));
        }

        List<PhotoPayload> photos = new ArrayList<>();
        for (Photo photo : photoRepository.findByAlbumId(albumId)) {
            String link = "/albums/" + album.getId() + "/photos/" + photo.getId() + "/download";
            photos.add(
                    PhotoPayload.builder()
                            .id(photo.getId())
                            .name(photo.getName())
                            .description(photo.getDescription())
                            .fileName(photo.getFileName())
                            .downloadLink(link)
                            .build()
            );
        }

        return AlbumPayload.builder()
                .id(album.getId())
                .name(album.getName())
                .description(album.getDescription())
                .photos(photos)
                .build();
    }
    public AlbumPayload create(AlbumDto albumDto, String email) throws ResourceNotFoundException {
        Album album = new Album();
        album.setName(albumDto.getName());
        album.setDescription(albumDto.getDescription());

        Account account = accountRepository.findByEmail(email)
                     .orElseThrow(() -> new ResourceNotFoundException("Failed to add album." +
                "Account not found for email: " + email));

        album.setAccount(account);

        Album savedAlbum = albumRepository.save(album);

        return new AlbumPayload(savedAlbum.getId(), savedAlbum.getName(), savedAlbum.getDescription(), null);
    }

    public AlbumPayload read(){
        return null;
    }
    public AlbumPayload updateAlbum(Long albumId, AlbumDto albumDto){
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found"));

        Long accountId = this.getCurrentLoggedInUser();
        // Access check
        if (!accountId.equals(album.getAccount().getId())) {
            throw new UnauthorizedAccessException(ErrorMessage.UNAUTHORIZED_ALBUM_ACCESS.format(albumId));
        }

        // Update album details
        album.setName(albumDto.getName());
        album.setDescription(albumDto.getDescription());

        // Save the updated album
        albumRepository.save(album);

        // Convert to AlbumPayload
        AlbumPayload albumPayload = AlbumPayload.builder()
                .id(album.getId())
                .name(album.getName())
                .description(album.getDescription())
                .photos(album.getPhotos().stream()
                        .map(photo -> PhotoPayload.builder()
                                .id(photo.getId())
                                .name(photo.getName())
                                .description(photo.getDescription())
                                .fileName(photo.getFileName())
                                .downloadLink("/albums/" + album.getId() + "/photos/" + photo.getId() + "/download")
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return albumPayload;
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
