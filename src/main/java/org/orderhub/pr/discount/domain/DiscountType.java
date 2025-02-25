package org.orderhub.pr.discount.domain;

public enum DiscountType {
    FIXED, // 고정 금액 할인
    PERCENTAGE, // 퍼센트 할인
    THRESHOLD_PRICE, // 최소 개수 기준 단가 조정
}
