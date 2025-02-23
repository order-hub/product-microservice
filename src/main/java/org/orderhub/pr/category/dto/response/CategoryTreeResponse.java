package org.orderhub.pr.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orderhub.pr.category.domain.Category;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTreeResponse {

    private Long id;
    private String name;
    private String categoryType;
    private List<CategoryTreeResponse> children;

    public CategoryTreeResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.categoryType = category.getType().name();
        this.children = category.getChildren().stream().map(CategoryTreeResponse::new).collect(Collectors.toList());
    }


}
