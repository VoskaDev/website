package com.voska.website.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String storeProjectImage(Long projectId, MultipartFile file);

    void delete(String imageUrl);
}
