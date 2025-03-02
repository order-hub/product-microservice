package org.orderhub.pr.discount.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionMessage {

    public static final String INVALID_DISCOUNT = "유효하지 않은 할인 정책입니다.";
    public static final String NOT_FOUND_DISCOUNT = "해당 할인 정책을 찾을 수 없습니다.";

}
