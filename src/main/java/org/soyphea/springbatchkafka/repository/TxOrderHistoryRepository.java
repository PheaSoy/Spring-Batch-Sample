package org.soyphea.springbatchkafka.repository;

import org.soyphea.springbatchkafka.entity.TxOrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TxOrderHistoryRepository extends JpaRepository<TxOrderHistory, Long> {
    Optional<TxOrderHistory> findByOrderId(Long id);
}