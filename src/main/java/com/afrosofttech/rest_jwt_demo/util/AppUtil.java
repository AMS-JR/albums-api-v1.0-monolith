package com.afrosofttech.rest_jwt_demo.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppUtil {
    public static void deleteFile(String fileName, String folderName, Long albumId) {
        Path filePath = Paths.get("src/main/resources/static/uploads/",
                albumId.toString(), folderName, fileName);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Handle the exception
        }
    }
    public static boolean deletePhotoFromPath(String path) {
        File file = new File(path);
        return file.delete();
    }
}
