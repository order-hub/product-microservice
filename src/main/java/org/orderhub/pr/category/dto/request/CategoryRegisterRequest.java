package org.orderhub.pr.category.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRegisterRequest {

    private String name;
    private Long parentCategoryId;
    private String categoryType;

}
