package org.orderhub.pr.product.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.orderhub.pr.product.domain.Product;
import org.orderhub.pr.product.domain.QProduct;
import org.orderhub.pr.product.repository.CustomProductRepository;
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

}
