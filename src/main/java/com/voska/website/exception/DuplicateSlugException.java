package com.voska.website.exception;

public class DuplicateSlugException extends RuntimeException {

    public DuplicateSlugException(String slug) {
        super("A project with this slug already exists: " + slug);
    }
}
