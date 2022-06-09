package org.soyphea.springbatchkafka.batch;

import lombok.extern.slf4j.Slf4j;
import org.soyphea.springbatchkafka.entity.TxOrder;
import org.soyphea.springbatchkafka.entity.TxOrderHistory;
import org.soyphea.springbatchkafka.exception.BatchRetryableException;
import org.soyphea.springbatchkafka.repository.TxOrderHistoryRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.apache.logging.log4j.ThreadContext.isEmpty;

@Slf4j
@Component
public class TxOrderProcessor implements ItemProcessor<TxOrder, TxOrderHistory> {

    @Autowired
    private TxOrderHistoryRepository txOrderHistoryRepository;

    @Override
    public TxOrderHistory process(TxOrder txOrder) throws Exception {
        return execute(txOrder);
    }

    @Transactional
    public TxOrderHistory execute(TxOrder txOrder) {
        log.info("Txn order processor.");
        if (txOrder.getName().equals("exception"))
            throw new BatchRetryableException("Retry!");
        Optional<TxOrderHistory> byOrderId =
                txOrderHistoryRepository.findByOrderId(txOrder.getId());
        if (byOrderId.isEmpty()) {
            TxOrderHistory txOrderHistory = new TxOrderHistory();
            txOrderHistory.setOrderId(txOrder.getId());
            txOrderHistory.setStatus("executing");
            return txOrderHistoryRepository.save(txOrderHistory);
        } else {
            TxOrderHistory txOrderHistory = byOrderId.get();
            if (txOrderHistory.getStatus().equals("failed")) {
                log.info("Retry the record!!!!");
                txOrderHistory.setStatus("executing");
                return txOrderHistoryRepository.save(txOrderHistory);
            }
        }
        return null;
    }
}
