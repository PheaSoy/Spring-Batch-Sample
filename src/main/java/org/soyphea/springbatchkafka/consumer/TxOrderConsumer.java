package org.soyphea.springbatchkafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.soyphea.springbatchkafka.constants.KafkaConstant;
import org.soyphea.springbatchkafka.entity.TxOrder;
import org.soyphea.springbatchkafka.entity.TxOrderHistory;
import org.soyphea.springbatchkafka.repository.TxOrderHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;

@Configuration
@Slf4j
public class TxOrderConsumer {

    @Autowired
    private TxOrderHistoryRepository txOrderHistoryRepository;

    @KafkaListener(topics = KafkaConstant.TOPIC)
    public void consume(Message<TxOrderHistory> txOrderMessage) {
        log.info("Tx order msg:{}", txOrderMessage);
        txOrderHistoryRepository.findById(txOrderMessage.getPayload().getId())
                .ifPresent(txOrderHistory -> {
                    txOrderHistory.setStatus("done");
                    txOrderHistoryRepository.save(txOrderHistory);
                });
    }
}
