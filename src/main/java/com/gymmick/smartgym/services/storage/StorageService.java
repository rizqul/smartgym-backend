package com.gymmick.smartgym.services.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String store(MultipartFile file, String location);

    void remove(String fileKey);
}
