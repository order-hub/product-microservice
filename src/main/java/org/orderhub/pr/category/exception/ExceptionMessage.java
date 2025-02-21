package org.orderhub.pr.category.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionMessage {

    public static final String NO_SUCH_CATEGORY = "해당하는 카테고리를 찾을 수 없습니다.";
    public static final String NO_SUCH_PARENT_CATEGORY = "요청하신 부모 카테고리를 찾을 수 없습니다.";
    public static final String CANNOT_BE_YOUR_OWN_PARENT = "부모 카테고리는 자기 자신일 수 없습니다.";
    public static final String CANNOT_BE_YOUR_OWN_CHILD = "자기 자신을 자식 카테고리로 추가할 수 없습니다.";

    public static final String MAJOR_CANNOT_BE_CHILD = "대분류는 자식 카테고리로 추가할 수 없습니다.";
    public static final String MINOR_CANNOT_BE_PARENT = "소분류는 부모 카테고리로 추가할 수 없습니다.";
}
