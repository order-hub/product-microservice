package org.orderhub.pr.discount.repository;

import org.orderhub.pr.discount.domain.BundleDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BundleDiscountRepository extends JpaRepository<BundleDiscount, Long> {

}
