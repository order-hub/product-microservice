package org.orderhub.pr.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.product.aop.annotation.ValidateImage;
import org.orderhub.pr.product.dto.request.ProductImageRegisterRequest;
import org.orderhub.pr.product.dto.request.ProductImageUpdateRequest;
import org.orderhub.pr.product.service.ProductImageUploadService;
import org.orderhub.pr.util.dto.InMemoryFile;
import org.orderhub.pr.util.service.InMemoryFileStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class S3ProductImageServiceImpl implements ProductImageUploadService {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private final S3Client s3Client;

    @ValidateImage
    public String registerProductImage(ProductImageRegisterRequest productImageRegisterRequest) throws IOException {
        String fileKey = generateFileKey(productImageRegisterRequest.getProductId(), productImageRegisterRequest.getStoredFile());
        return uploadToS3(productImageRegisterRequest.getStoredFile(), fileKey);
    }

    @ValidateImage
    public String updateProductImage(ProductImageUpdateRequest productImageUpdateRequest) throws IOException {
        String fileKey = generateFileKey(productImageUpdateRequest.getProductId(), productImageUpdateRequest.getStoredFile());
        return uploadToS3(productImageUpdateRequest.getStoredFile(), fileKey);
    }

    private String generateFileKey(Long productId, InMemoryFile file) {
        return "products/" + productId + "/thumbnail." + getFileExtension(file.getOriginalFilename());
    }

    private String uploadToS3(InMemoryFile file, String fileKey) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(file.getContentType())
                .build();
        s3Client.putObject(putObjectRequest,
                software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getContent()));
        S3Utilities s3Utilities = s3Client.utilities();

        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        URL fileUrl = s3Utilities.getUrl(getUrlRequest);
        return fileUrl.toExternalForm();
    }

    private String getFileExtension(String originalFileName) {
        return originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
