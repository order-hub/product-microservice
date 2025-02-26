package org.orderhub.pr.discount.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OrderDiscountTest {

    private OrderDiscount orderDiscount;

    @BeforeEach
    void setUp() {
        orderDiscount = new OrderDiscount();
        orderDiscount.onCreate(); // 생성 시간 초기화

        orderDiscount = spy(orderDiscount);
        doReturn(Instant.now().minus(1, ChronoUnit.DAYS)).when(orderDiscount).getStartDate();
        doReturn(Instant.now().plus(1, ChronoUnit.DAYS)).when(orderDiscount).getEndDate();
    }

    @Test
    void testOrderDiscountCreation() {
        assertThat(orderDiscount).isNotNull();
    }

    @Test
    void testCreatedAtAndUpdatedAtAreSetOnCreate() {
        orderDiscount.onCreate();

        assertThat(orderDiscount.getCreatedAt()).isNotNull();
        assertThat(orderDiscount.getUpdatedAt()).isNotNull();
    }

    @Test
    void testUpdatedAtIsUpdatedOnUpdate() throws InterruptedException {
        orderDiscount.onCreate();
        Instant firstUpdatedAt = orderDiscount.getUpdatedAt();

        // 1초 대기 후 업데이트 호출
        Thread.sleep(1000);
        orderDiscount.onUpdate();

        assertThat(orderDiscount.getUpdatedAt()).isAfter(firstUpdatedAt);
    }

    @Test
    void testIsActive_WhenWithinDiscountPeriod() {
        doReturn(Instant.now().minus(1, ChronoUnit.DAYS)).when(orderDiscount).getStartDate();
        doReturn(Instant.now().plus(1, ChronoUnit.DAYS)).when(orderDiscount).getEndDate();

        assertThat(orderDiscount.isActive()).isTrue();
    }

    @Test
    void testIsActive_WhenBeforeDiscountPeriod() {
        doReturn(Instant.now().plus(1, ChronoUnit.DAYS)).when(orderDiscount).getStartDate();
        doReturn(Instant.now().plus(2, ChronoUnit.DAYS)).when(orderDiscount).getEndDate();

        assertThat(orderDiscount.isActive()).isFalse();
    }

    @Test
    void testIsActive_WhenAfterDiscountPeriod() {
        doReturn(Instant.now().minus(2, ChronoUnit.DAYS)).when(orderDiscount).getStartDate();
        doReturn(Instant.now().minus(1, ChronoUnit.DAYS)).when(orderDiscount).getEndDate();

        assertThat(orderDiscount.isActive()).isFalse();
    }
}
