package org.orderhub.pr.discount.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.orderhub.pr.discount.domain.BundleDiscount;
import org.orderhub.pr.discount.domain.BundleDiscountProduct;
import org.orderhub.pr.discount.dto.request.BundleDiscountCreateRequest;
import org.orderhub.pr.discount.dto.request.BundleDiscountUpdateRequest;
import org.orderhub.pr.discount.dto.response.BundleDiscountResponse;
import org.orderhub.pr.discount.repository.BundleDiscountProductRepository;
import org.orderhub.pr.discount.repository.BundleDiscountRepository;
import org.orderhub.pr.discount.service.BundleDiscountService;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BundleDiscountServiceImpl implements BundleDiscountService {

    private final BundleDiscountRepository bundleDiscountRepository;
    private final BundleDiscountProductRepository bundleDiscountProductRepository;
    private final ProductRepository productRepository;

    public BundleDiscountResponse createBundleDiscount(BundleDiscountCreateRequest request) {
        List<Product> products = productRepository.findAllById(request.getProductIds());

        BundleDiscount bundleDiscount = BundleDiscount.builder()
                .discountValue(request.getDiscountValue())
                .discountType(request.getDiscountType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        List<BundleDiscountProduct> bundleProducts = products.stream()
                .map(product -> new BundleDiscountProduct(bundleDiscount, product))
                .collect(Collectors.toList());

        bundleDiscount.getBundleProducts().addAll(bundleProducts);

        bundleDiscountRepository.save(bundleDiscount);
        bundleDiscountProductRepository.saveAll(bundleProducts);

        return toResponse(bundleDiscount);
    }

    public BundleDiscountResponse getBundleDiscount(Long id) {
        BundleDiscount bundleDiscount = bundleDiscountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BundleDiscount not found: " + id));
        return toResponse(bundleDiscount);
    }

    @Transactional(readOnly = true)
    public List<BundleDiscountResponse> getAllBundleDiscounts() {
        List<BundleDiscount> discounts = bundleDiscountRepository.findAll();
        return discounts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BundleDiscountResponse updateBundleDiscount(Long id, BundleDiscountUpdateRequest request) {
        BundleDiscount bundleDiscount = bundleDiscountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BundleDiscount not found: " + id));

        bundleDiscountProductRepository.deleteByBundleDiscount(bundleDiscount);
        bundleDiscount.getBundleProducts().clear();

        List<Product> products = productRepository.findAllById(request.getProductIds());
        BundleDiscount finalBundleDiscount = bundleDiscount;
        List<BundleDiscountProduct> bundleProducts = products.stream()
                .map(product -> BundleDiscountProduct.builder()
                        .bundleDiscount(finalBundleDiscount)
                        .product(product)
                        .build()
                )
                .collect(Collectors.toList());

        bundleDiscount.getBundleProducts().clear();
        bundleDiscount.getBundleProducts().addAll(bundleProducts);

        bundleDiscount = BundleDiscount.builder()
                .discountValue(request.getDiscountValue())
                .discountType(request.getDiscountType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .build();

        bundleDiscountRepository.save(bundleDiscount);
        bundleDiscountProductRepository.saveAll(bundleProducts);

        return toResponse(bundleDiscount);
    }

    public void deleteBundleDiscount(Long id) {
        BundleDiscount bundleDiscount = bundleDiscountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BundleDiscount not found: " + id));

        bundleDiscountProductRepository.deleteByBundleDiscount(bundleDiscount);

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
