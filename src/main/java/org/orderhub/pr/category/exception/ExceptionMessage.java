package org.orderhub.pr.category.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionMessage {

    public static final String NO_SUCH_CATEGORY = "해당하는 카테고리를 찾을 수 없습니다.";
    public static final String NO_SUCH_PARENT_CATEGORY = "요청하신 부모 카테고리를 찾을 수 없습니다.";

}
