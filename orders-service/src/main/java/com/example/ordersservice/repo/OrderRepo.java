package com.example.ordersservice.repo;

import com.example.ordersservice.domain.Order;
import com.example.ordersservice.domain.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

import java.util.List;

@Repository
@Transactional
public interface OrderRepo extends JpaRepository<Order, Long> {

    List<Order> findByStatusAndDriverIsNull(OrderStatus status);

    List<Order> findByDriver(String driver, Pageable pageable);

}
