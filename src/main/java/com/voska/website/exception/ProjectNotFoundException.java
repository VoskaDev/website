package com.voska.website.exception;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(Long id) {
        super("Project not found with id: " + id);
    }

    public ProjectNotFoundException(String slug) {
        super("Project not found with slug: " + slug);
    }
}
