package org.orderhub.pr.product.domain;

public enum SaleStatus {
    FOR_SALE,      // 판매 중
    OUT_OF_STOCK,  // 품절
    DISCONTINUED,  // 단종
    PREORDER,      // 예약 판매
    BACKORDER,      // 재입고 예정
    DELETED,        // 삭제
}
