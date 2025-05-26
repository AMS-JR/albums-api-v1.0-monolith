package com.afrosofttech.rest_jwt_demo.util.constants;

public enum FileType {
    PHOTO("photos"),
    THUMBNAIL("thumbnails");

    private final String folderName;

    FileType(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}
