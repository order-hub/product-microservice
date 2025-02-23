package org.orderhub.pr.product.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.QProduct;
import org.orderhub.pr.product.dto.request.ProductSearchRequest;
import org.orderhub.pr.product.repository.CustomProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Product> findByJsonAttributes(Map<String, Object> attributes) {
        QProduct product = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String jsonCondition = "{\"" + entry.getKey() + "\": \"" + entry.getValue() + "\"}";
            BooleanExpression condition = Expressions.booleanTemplate("attributes @> {0}", jsonCondition);
            builder.and(condition);
        }

        return queryFactory.selectFrom(product)
                .where(builder)
                .fetch();
    }

    public Page<Product> searchProducts(ProductSearchRequest criteria, Pageable pageable) {
        QProduct product = QProduct.product;
        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.getName() != null) {
            builder.and(product.name.containsIgnoreCase(criteria.getName()));
        }

        if (criteria.getCategoryName() != null) {
            builder.and(product.category.name.eq(criteria.getCategoryName()));
        }

        if (criteria.getSaleStatus() != null) {
            builder.and(product.saleStatus.eq(criteria.getSaleStatus()));
        }

        if (criteria.getConditionStatus() != null) {
            builder.and(product.conditionStatus.eq(criteria.getConditionStatus()));
        }

        if (criteria.getPriceMin() != null && criteria.getPriceMax() != null) {
            builder.and(product.price.between(criteria.getPriceMin(), criteria.getPriceMax()));
        }

        if (criteria.getAttributes() != null) {
            for (Map.Entry<String, Object> entry : criteria.getAttributes().entrySet()) {
                String jsonCondition = "{\"" + entry.getKey() + "\": \"" + entry.getValue() + "\"}";
                BooleanExpression condition = Expressions.booleanTemplate("attributes @> {0}", jsonCondition);
                builder.and(condition);
            }
        }

        List<Product> products = queryFactory.selectFrom(product)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(product.count())
                .from(product)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(products, pageable, total != null ? total : 0);
    }


}
