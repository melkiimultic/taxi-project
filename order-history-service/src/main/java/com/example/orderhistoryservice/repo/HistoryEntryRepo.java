package com.example.orderhistoryservice.repo;

import com.example.orderhistoryservice.domain.HistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface HistoryEntryRepo extends JpaRepository<HistoryEntry,Long> {

    List <HistoryEntry> findAllByOrderId(Long orderId);
}
