package org.soyphea.springbatchkafka.repository;

import org.soyphea.springbatchkafka.entity.TxOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TxOrderRepository extends PagingAndSortingRepository<TxOrder, Integer> {

    @Query("select t from TxOrder t")
    List<TxOrder> findByAmount();

    List<TxOrder> findByStatus(String status);

}