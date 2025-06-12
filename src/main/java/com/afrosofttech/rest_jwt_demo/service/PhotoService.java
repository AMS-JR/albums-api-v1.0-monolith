package com.afrosofttech.rest_jwt_demo.service;

import com.afrosofttech.rest_jwt_demo.dto.photo.request.PhotoRequestDto;
import com.afrosofttech.rest_jwt_demo.entity.Account;
import com.afrosofttech.rest_jwt_demo.entity.Album;
import com.afrosofttech.rest_jwt_demo.entity.Photo;
import com.afrosofttech.rest_jwt_demo.exception.*;
import com.afrosofttech.rest_jwt_demo.repository.AccountRepository;
import com.afrosofttech.rest_jwt_demo.repository.AlbumRepository;
import com.afrosofttech.rest_jwt_demo.repository.PhotoRepository;
import com.afrosofttech.rest_jwt_demo.util.AppUtil;
import com.afrosofttech.rest_jwt_demo.util.constants.FileType;
import com.afrosofttech.rest_jwt_demo.util.constants.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@Slf4j
public class PhotoService {
    private final int THUMBNAIL_WIDTH=300;
    private final PhotoRepository photoRepository;
    private final AccountRepository accountRepository;
    private final AlbumRepository albumRepository;

    public PhotoService(PhotoRepository photoRepository, AccountRepository accountRepository, AlbumRepository albumRepository) {
        this.photoRepository = photoRepository;
        this.accountRepository = accountRepository;
        this.albumRepository = albumRepository;
    }

    public Map<String, List<String>> savePhotos(Long albumId, MultipartFile[] files, Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        Account account = optionalAccount.get();

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.ALBUM_NOT_FOUND.format(albumId)));

        if (!account.getId().equals(album.getAccount().getId())) {
            throw new UnauthorizedAccessException(ErrorMessage.UNAUTHORIZED_ALBUM_ACCESS.format(albumId));
        }
        List<String> successfulUploads = new ArrayList<>();
        List<String> failedUploads = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                // Validate file type and size
                if (!isImage(file)) {
                    throw new UnsupportedMediaTypException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE.format());
                }


                // Generate unique file name
                String finalFileName = generateFileName(file.getOriginalFilename());
                String uploadPath = this.getPhotoUploadPath(finalFileName,
                        FileType.PHOTO.getFolderName(), albumId);
                Path path = Paths.get(uploadPath);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                // Save file metadata to the database
                Photo photo = Photo.builder()
                        .name(finalFileName)
                        .originalFileName(file.getOriginalFilename())
                        .fileName(finalFileName)
                        .album(album)
                        .build();

                this.save(photo);
                try {
                    BufferedImage thumbImage = createThumbnail(file);
                    String extension = file.getContentType().split("/")[1];
                    String thumbnailLocation = this.getPhotoUploadPath(finalFileName,
                            FileType.THUMBNAIL.getFolderName(), albumId);
                    ImageIO.write(thumbImage, extension, new File(thumbnailLocation));
                } catch (Exception e) {
                    log.debug("Photo upload error: " + e.getMessage());
                    failedUploads.add(file.getOriginalFilename());
                }
                successfulUploads.add(finalFileName);
            }  catch (UnsupportedMediaTypException e) {
                log.warn(e.getMessage(), file.getOriginalFilename());
                failedUploads.add(file.getOriginalFilename());
            }  catch (Exception e) {
                log.error("General error uploading file {}: {}", file.getOriginalFilename(), e.getMessage());
                failedUploads.add(file.getOriginalFilename());
            }
        }

        Map<String, List<String>> result = new HashMap<>();
        result.put("success", successfulUploads);
        result.put("failed", failedUploads);
        return result;
    }
    public Resource downloadFile(Long albumId, Long photoId, FileType type){
        // Authentication and authorization
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.ALBUM_NOT_FOUND.format(albumId)));

        if (!album.getAccount().equals(account)) {
            throw new UnauthorizedAccessException(ErrorMessage.UNAUTHORIZED_ALBUM_ACCESS.format(albumId));
        }

        // Retrieve photo
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found"));

        if (!photo.getAlbum().getId().equals(albumId)) {
            throw new UnauthorizedAccessException(ErrorMessage.UNAUTHORIZED_PHOTO_ACCESS.format(photoId));
        }
        Resource resource;
        String fileType = type.equals(FileType.PHOTO)? FileType.PHOTO.getFolderName():
                FileType.THUMBNAIL.getFolderName();
        try {
            resource = this.getFileResource(albumId, fileType, photo.getFileName());
        } catch (IOException e) {
            throw new ResourceNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND.format());
        }

        if (resource == null || !resource.exists()) {
            throw new ResourceNotFoundException("File not found");
        }
        return resource;
    }
    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif");
    }
    public Map<String, String> updatePhoto(Long albumId, Long photoId, PhotoRequestDto photoRequestDto) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND.format(albumId)));

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.RESOURCE_NOT_FOUND.format(photoId)));

        if (!album.getPhotos().contains(photo)) {
            throw new BadRequestException(ErrorMessage.BAD_REQUEST.format());
        }

        photo.setName(photoRequestDto.getName());
        photo.setDescription(photoRequestDto.getDescription());
        photoRepository.save(photo);

        Map<String, String> response = new HashMap<>();
        response.put("success", "update successful");
        return response;
    }
    public Map<String, String> deletePhoto(Long albumId, long photoId){
        try {
            // Authentication and Ownership Check
            Long accountId = this.getCurrentLoggedInUser();
            Album album = albumRepository.findById(albumId).get();
            if (!album.getAccount().getId().equals(accountId)) {
                throw new UnauthorizedAccessException(ErrorMessage.UNAUTHORIZED_PHOTO_DELETE.format());
            }

            // Photo Validation
            Photo photo = photoRepository.findById(photoId).get();
            if (!photo.getAlbum().getId().equals(albumId)) {
                throw new BadRequestException(ErrorMessage.BAD_REQUEST.format());
            }

            // Deletion Process
            AppUtil.deletePhotoFromPath("src/main/resources/static/uploads/" + albumId + "/" +
                    FileType.PHOTO.getFolderName() + "/" + photo.getFileName());

            AppUtil.deletePhotoFromPath("src/main/resources/static/uploads/" + albumId + "/" +
                    FileType.THUMBNAIL.getFolderName() + "/" + photo.getFileName());

            photoRepository.deleteById(photoId);

            // Confirm deletion
            boolean stillExists = photoRepository.existsById(photoId);
            if (stillExists) {
                throw new BadRequestException(ErrorMessage.PHOTO_DELETION_FAILED.format());
            }

            Map<String, String> response = new HashMap<>();
            response.put("success", "Photo deleted successfully!");
            return response;
        } catch (Exception e) {
            throw new BadRequestException(ErrorMessage.PHOTO_DELETION_INTERRUPTED.format());
        }
    }
    private String generateFileName(String originalFileName) {
        return System.currentTimeMillis() + "_" + originalFileName;
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
//        private String generateFileName(String filename){
//        String originalFilename = filename;
//        String randomString = RandomStringUtils.randomAlphanumeric(10);
//        String finalFilename = randomString + originalFilename;
//        return finalFilename;
//    }
    private static String getPhotoUploadPath(String filename, String folderName, Long albumId) {
        String uploadDir = "src/main/resources/static/uploads/" + albumId + "/" + folderName;
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return uploadDir + "/" + filename;
    }
    public BufferedImage createThumbnail(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        BufferedImage thumbnail = Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, THUMBNAIL_WIDTH);
        return thumbnail;
    }
    private Resource getFileResource(Long albumId, String folderName, String fileName) throws IOException {
        Path filePath = Paths.get("src/main/resources/static/uploads")
                .resolve(albumId.toString())
                .resolve(folderName)
                .resolve(fileName)
                .normalize();

        if (!Files.exists(filePath)) {
            return null;
        }

        return new UrlResource(filePath.toUri());
    }
    private Photo save(Photo photo){
        return photoRepository.save(photo);
    }
    public Optional<Photo> findById(Long id){
        return photoRepository.findById(id);
    }
    public List<Photo> findByAlbumId(Long albumId){
        return photoRepository.findByAlbumId(albumId);
    }
}
