package org.orderhub.pr.discount.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.orderhub.pr.discount.domain.BundleDiscount;
import org.orderhub.pr.discount.domain.DiscountStatus;
import org.orderhub.pr.discount.dto.request.BundleDiscountCreateRequest;
import org.orderhub.pr.discount.dto.request.BundleDiscountUpdateRequest;
import org.orderhub.pr.discount.dto.response.BundleDiscountResponse;
import org.orderhub.pr.discount.repository.BundleDiscountRepository;
import org.orderhub.pr.discount.service.BundleDiscountService;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.repository.ProductRepository;
import org.orderhub.pr.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BundleDiscountServiceImpl implements BundleDiscountService {

    private final BundleDiscountRepository bundleDiscountRepository;
    private final ProductService productService;

    @Transactional
    public BundleDiscountResponse createBundleDiscount(BundleDiscountCreateRequest request) {
        List<Product> products = productService.findAllById(request.getProductIds());

        if (products.isEmpty()) {
            throw new EntityNotFoundException("No products found for given IDs: " + request.getProductIds());
        }

        BundleDiscount bundleDiscount = BundleDiscount.builder()
                .discountValue(request.getDiscountValue())
                .discountType(request.getDiscountType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(DiscountStatus.ACTIVE)
                .build();

        bundleDiscount.addProducts(products);

        bundleDiscountRepository.save(bundleDiscount);

        return toResponse(bundleDiscount);
    }

    public BundleDiscountResponse getBundleDiscount(Long id) {
        BundleDiscount bundleDiscount = bundleDiscountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BundleDiscount not found: " + id));
        return toResponse(bundleDiscount);
    }

    public List<BundleDiscountResponse> getAllBundleDiscounts() {
        return bundleDiscountRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BundleDiscountResponse updateBundleDiscount(Long id, BundleDiscountUpdateRequest request) {
        BundleDiscount bundleDiscount = bundleDiscountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BundleDiscount not found: " + id));

        bundleDiscount.clearProducts();

        List<Product> products = productService.findAllById(request.getProductIds());
        bundleDiscount.addProducts(products);

        bundleDiscount = BundleDiscount.builder()
                .discountValue(request.getDiscountValue())
                .discountType(request.getDiscountType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .build();

        return toResponse(bundleDiscount);
    }

    @Transactional
    public void deleteBundleDiscount(Long id) {
        BundleDiscount bundleDiscount = bundleDiscountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BundleDiscount not found: " + id));

        bundleDiscountRepository.delete(bundleDiscount);
    }

    private BundleDiscountResponse toResponse(BundleDiscount bundleDiscount) {
        return BundleDiscountResponse.builder()
                .id(bundleDiscount.getId())
                .discountValue(bundleDiscount.getDiscountValue())
                .discountType(bundleDiscount.getDiscountType())
                .productIds(bundleDiscount.getBundleProducts().stream()
                        .map(bundleProduct -> bundleProduct.getProduct().getId())
                        .collect(Collectors.toList()))
                .startDate(bundleDiscount.getStartDate())
                .endDate(bundleDiscount.getEndDate())
                .status(bundleDiscount.getStatus())
                .createdAt(bundleDiscount.getCreatedAt())
                .updatedAt(bundleDiscount.getUpdatedAt())
                .build();
    }
}
