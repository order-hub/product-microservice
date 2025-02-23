package org.orderhub.pr.util.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionMessage {
    public static String COVERT_JSON_EXCEPTION_MESSAGE = "해당 JSON 객체를 COVERT 할 수 없습니다.";
}
