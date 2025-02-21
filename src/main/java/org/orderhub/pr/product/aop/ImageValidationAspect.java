package org.orderhub.pr.product.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.orderhub.pr.product.exception.ExceptionMessage.*;

@Aspect
@Component
@RequiredArgsConstructor
public class ImageValidationAspect {

    @Value("${aws.s3.max-file-size}")
    private long maxFileSize;

    @Value("${aws.s3.supported-extensions}")
    private String supportedExtensions;

    @Around("@annotation(org.orderhub.pr.product.aop.annotation.ValidateImage)")
    public Object validateImageSize(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();

        Set<String> validExtensions = Arrays.stream(supportedExtensions.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        Arrays.stream(args).forEach(arg -> {
            if (arg instanceof MultipartFile file) {
                validateFileSize(file);
                validateFileExtension(file, validExtensions);
            }
        });
        return joinPoint.proceed(args);
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(FILE_SIZE_EXCEEDED);
        }
    }

    private void validateFileExtension(MultipartFile file, Set<String> validExtensions) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException(INVALID_FILE_FORMAT);
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        if (!validExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException(UNSUPPORTED_FILE_EXTENSIONS);
        }
    }

}
