package com.afrosofttech.rest_jwt_demo.util.constants.exceptions;

public enum ErrorMessage {
    ALBUM_NOT_FOUND("Failed to find album with id %d"),
    UNAUTHORIZED_ALBUM_ACCESS("You are not authorized to modify album with id %d"),
    UNAUTHORIZED_ALBUM_DELETE("You are not authorized to delete this photo."),
    UNAUTHORIZED_PHOTO_ACCESS("You are not authorized to access photo with id %d"),
    UNAUTHORIZED_PHOTO_DELETE("You are not authorized to delete this photo."),
    UNSUPPORTED_MEDIA_TYPE("Invalid image file type"),
    PHOTO_DELETION_FAILED("Failed to delete photo."),
    PHOTO_DELETION_INTERRUPTED("An error occurred while deleting the photo."),
    BAD_REQUEST("Photo does not belong to the specified album"),
    RESOURCE_NOT_FOUND("Resource not found");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}
