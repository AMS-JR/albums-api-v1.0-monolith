//package com.afrosofttech.rest_jwt_demo.controller;
//
//import com.afrosofttech.rest_jwt_demo.service.AccountService;
//import com.afrosofttech.rest_jwt_demo.service.AlbumService;
//import com.afrosofttech.rest_jwt_demo.service.PhotoService;
//import io.swagger.v3.oas.annotations.Operation;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/photos")
//public class PhotoController {
//    private final PhotoService photoService;
//    private final AccountService accountService;
//    private final AlbumService albumService;
//
//    public PhotoController(PhotoService photoService, AccountService accountService, AlbumService albumService) {
//        this.photoService = photoService;
//        this.accountService = accountService;
//        this.albumService = albumService;
//    }

//    @GetMapping
//    public List<ExamplePayload> getAllExamples() {} // similar to Ruby's `index`
//
//    @GetMapping("/{id}")
//    public ExamplePayload getExamplerById(@PathVariable Long id) {} // similar to `show`
//
//    @PostMapping(value = "/photos", consumes = {"multipart/form-data"})
//    @Operation(summary = "Upload photo in to album")
//    public List<String> createExample(@RequestPart("files") MultipartFile[] file) {
//        return albumService.savePhotos(files);
//    }

//    @PutMapping("/{id}")
//    public ExamplePayload updateExample(@PathVariable Long id, @RequestBody ExampleDto exampleDto) {} // `update`

//    @DeleteMapping("/{id}")
//    public void deleteExample(@PathVariable Long id) {} // `destroy`
//}
