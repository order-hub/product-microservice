package org.orderhub.pr.order.domain;

public enum OrderStatus {
    PENDING, // 결제 전 주문 생성
    PAID, // 결제 완료
    PROCESSING, // 주문 처리 중(상품 처리 중)
    SHIPPED, // 배송 됨
    DELIVERED, // 고객 수령 완료
    CANCELLED, // 주문 취소
    RETURNED, // 반품
    REFUNDED, // 환불 완료
}
