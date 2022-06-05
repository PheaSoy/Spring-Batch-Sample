package org.soyphea.springbatchkafka.batch;

import lombok.extern.slf4j.Slf4j;
import org.soyphea.springbatchkafka.entity.TxOrder;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class TxOrderProcessorTransaction implements ItemProcessor<TxOrder, TxOrder> {

    @Override
    public TxOrder process(TxOrder txOrder) throws Exception {
        execute(txOrder);
        return txOrder;
    }

    public void execute(TxOrder txOrder) {
        log.info("Process Transaction with txOrder ID:"+txOrder.getId());
        txOrder.setStatus("done");
    }
}
