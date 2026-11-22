package com.shop.SweetStory.order.repository;

import com.shop.SweetStory.auth.entity.User;
import com.shop.SweetStory.order.entity.Order;
import com.shop.SweetStory.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Long countByDeliveryDateAndStatusNot(LocalDate deliveryDate, OrderStatus status);

    @EntityGraph(attributePaths = {"product", "user"})
    List<Order> getAllByUserIdOrderByDeliveryDateDesc(Long userId);

    @EntityGraph(attributePaths = {"product", "user"})
    List<Order> findAllByOrderByDeliveryDateAscIdAsc();
}
