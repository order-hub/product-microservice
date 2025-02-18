package org.orderhub.pr.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orderhub.pr.category.domain.Category;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRegisterResponse {

    private Long id;
    private Long parentId;
    private String name;
    private String categoryType;

    public static CategoryRegisterResponse of(Category category) {
        return CategoryRegisterResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParent() == null ? null : category.getParent().getId())
                .categoryType(category.getType().name())
                .build();
    }

}
