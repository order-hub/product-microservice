package org.orderhub.pr.system.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Component
public class ImageConfig {
    @Value("${aws.s3.max-file-size}")
    private long maxFileSize;

    @Value("${aws.s3.supported-extensions}")
    private String supportedExtensions;

    public Set<String> getSupportedExtensionsSet() {
        return Arrays.stream(supportedExtensions.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }
}
