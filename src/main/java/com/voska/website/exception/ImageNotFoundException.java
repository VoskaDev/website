package com.voska.website.exception;

public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException(Long id) {
        super("Project image not found with id: " + id);
    }
}
