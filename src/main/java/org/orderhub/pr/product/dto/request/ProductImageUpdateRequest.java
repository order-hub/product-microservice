package org.orderhub.pr.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orderhub.pr.util.dto.InMemoryFile;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageUpdateRequest {
    private Long productId;
    private InMemoryFile storedFile;
}
