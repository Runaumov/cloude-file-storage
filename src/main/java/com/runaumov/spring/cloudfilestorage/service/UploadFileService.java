package com.runaumov.spring.cloudfilestorage.service;

import com.runaumov.spring.cloudfilestorage.dto.ResourceResponseDto;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UploadFileService {

    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucketName;

    public List<ResourceResponseDto> uploadFiles(String path, List<MultipartFile> files) {
        List<ResourceResponseDto> uploadFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String objectName = path.endsWith("/") ? path + file.getOriginalFilename() : path + "/" + file.getOriginalFilename();

                try (InputStream inputStream = file.getInputStream()) {
                    minioClient.putObject(
                            PutObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(objectName)
                                    .stream(inputStream, file.getSize(), -1)
                                    .contentType(file.getContentType())
                                    .build()
                    );
                }

                ResourceResponseDto responseDto = new ResourceResponseDto();
                responseDto.setPath(objectName.substring(0, objectName.lastIndexOf("/") + 1));
                responseDto.setName(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("/") + 1));
                responseDto.setSize(file.getSize());
                responseDto.setType("FILE");

                uploadFiles.add(responseDto);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при загрузке файла");
            }
        }
        return uploadFiles;
    }

}
