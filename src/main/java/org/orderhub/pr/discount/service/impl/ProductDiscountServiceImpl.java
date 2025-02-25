package org.orderhub.pr.discount.service.impl;

import lombok.RequiredArgsConstructor;
import org.orderhub.pr.discount.domain.DiscountStatus;
import org.orderhub.pr.discount.domain.DiscountType;
import org.orderhub.pr.discount.domain.ProductDiscount;
import org.orderhub.pr.discount.dto.request.ProductDiscountCreateRequest;
import org.orderhub.pr.discount.dto.response.ProductDiscountResponse;
import org.orderhub.pr.discount.repository.ProductDiscountRepository;
import org.orderhub.pr.discount.service.ProductDiscountService;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.orderhub.pr.discount.exception.ExceptionMessage.INVALID_DISCOUNT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductDiscountServiceImpl implements ProductDiscountService {

    private final ProductDiscountRepository productDiscountRepository;
    private final ProductService productService;

    public Optional<ProductDiscountResponse> getActiveDiscountByProduct(Product product) {
        return productDiscountRepository.findByProductAndStatus(product, DiscountStatus.ACTIVE)
                .map(ProductDiscountResponse::from);
    }

    public List<ProductDiscountResponse> getAllActiveDiscounts() {
        return productDiscountRepository.findAllByStatus(DiscountStatus.ACTIVE).stream()
                .map(ProductDiscountResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDiscountResponse createDiscount(ProductDiscountCreateRequest request) {
        ProductDiscount productDiscount = ProductDiscount.builder()
                .discountType(DiscountType.fromString(request.getDiscountType()))
                .discountUnitPrice(request.getDiscountUnitPrice())
                .thresholdQuantity(request.getThresholdQuantity())
                .discountValue(request.getDiscountValue())
                .product(productService.getProductById(request.getProductId()))
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        ProductDiscount saved = productDiscountRepository.save(productDiscount);
        return ProductDiscountResponse.from(saved);
    }



    @Transactional
    public void deleteDiscount(Long id) {
        ProductDiscount productDiscount = productDiscountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_DISCOUNT));
        productDiscount.delete();
    }

    @Transactional
    public void restoreDiscount(Long id) {
        ProductDiscount productDiscount = productDiscountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_DISCOUNT));
        productDiscount.restore();
    }


}
