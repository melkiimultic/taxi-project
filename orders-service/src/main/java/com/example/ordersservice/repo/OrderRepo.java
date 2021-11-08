package com.example.ordersservice.repo;

import com.example.ordersservice.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface OrderRepo extends JpaRepository<Order,Long> {

    List<Order> findByStatus(String status);

}
