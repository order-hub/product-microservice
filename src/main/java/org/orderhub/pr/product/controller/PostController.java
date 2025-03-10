package org.orderhub.pr.product.controller;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.product.dto.request.ProductRegisterRequest;
import org.orderhub.pr.product.dto.request.ProductSearchRequest;
import org.orderhub.pr.product.dto.request.ProductUpdateRequest;
import org.orderhub.pr.product.dto.response.ProductResponse;
import org.orderhub.pr.product.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/deleted")
    public ResponseEntity<Page<ProductResponse>> getDeletedProducts(Pageable pageable) {
        Page<ProductResponse> products = productService.getDeletedProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            Pageable pageable,
            @ModelAttribute ProductSearchRequest searchRequest
    ) {
        Page<ProductResponse> products = productService.getProductByPage(pageable, searchRequest);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ProductResponse.from(productService.getProductById(id)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
            @RequestPart("request") ProductRegisterRequest request,
            @RequestPart(value = "productImage", required = false) MultipartFile productImage
    ) {
        ProductResponse createdProduct = productService.createProduct(request, productImage);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @RequestBody ProductUpdateRequest request,
            @PathVariable Long id
    ) {
        ProductResponse updatedProduct = productService.updateProduct(request, id);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }


}
