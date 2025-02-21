package org.orderhub.pr.product.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionMessage {
    public static final String PRODUCT_NOT_FOUND = "해당 상품을 찾을 수 없습니다.";

    public static final String FILE_SIZE_EXCEEDED = "파일 사이즈가 기준치를 초과하였습니다.";
    public static final String INVALID_FILE_FORMAT = "잘못된 파일 형식입니다.";
    public static final String UNSUPPORTED_FILE_EXTENSIONS = "지원하지 않는 파일 확장자입니다.";
}
