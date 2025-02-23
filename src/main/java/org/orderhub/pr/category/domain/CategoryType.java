package org.orderhub.pr.category.domain;

public enum CategoryType {
    MAJOR, // 대분류
    MIDDLE, // 중분류
    MINOR; // 소분류

    public static CategoryType fromString(String string) {
        if (string == null) {
            return null;
        }
        return CategoryType.valueOf(string.toUpperCase());
    }

}
