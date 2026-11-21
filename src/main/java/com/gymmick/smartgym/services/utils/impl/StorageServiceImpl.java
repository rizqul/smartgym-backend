package com.gymmick.smartgym.services.utils.impl;

import java.io.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.gymmick.smartgym.services.utils.StorageService;

import io.awspring.cloud.s3.S3Exception;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    @Value("${app.config.aws.s3.bucket}")
    private String storageBucket;

    private final S3Client s3Client;

    @Override
    public String store(MultipartFile file, String location) {

        try (InputStream inputStream = file.getInputStream()) {

            String fileName = UUID.randomUUID().toString().replaceAll("-", "");
            String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());

            String fileKey = String.format("%s/%s.%s", location, fileName, fileExtension);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(storageBucket)
                    .key(fileKey)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromContentProvider(
                    ContentStreamProvider.fromInputStream(inputStream),
                    file.getSize(),
                    file.getContentType()));

            return fileKey;

        } catch (Exception e) {
            throw new S3Exception("Gagal mengunggah berkas file!", e.getCause());
        }
    }

    @Override
    public void remove(String fileKey) {

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(storageBucket)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

        } catch (Exception e) {
            throw new S3Exception("Gagal menghapus berkas file!", e.getCause());
        }
    }
}
