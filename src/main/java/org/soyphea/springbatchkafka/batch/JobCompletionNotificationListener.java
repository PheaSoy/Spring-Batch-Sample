package org.soyphea.springbatchkafka.batch;

import lombok.extern.slf4j.Slf4j;
import org.soyphea.springbatchkafka.entity.TxOrder;
import org.soyphea.springbatchkafka.entity.TxOrderHistory;
import org.soyphea.springbatchkafka.repository.TxOrderHistoryRepository;
import org.soyphea.springbatchkafka.repository.TxOrderRepository;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final TxOrderHistoryRepository txOrderHistory;

    public JobCompletionNotificationListener(TxOrderHistoryRepository txOrderRepository) {
        this.txOrderHistory = txOrderRepository;
    }

    // The callback method from the Spring Batch JobExecutionListenerSupport class that is executed when the batch process is completed
    @Override
    public void afterJob(JobExecution jobExecution) {
        // When the batch process is completed the the users in the database are retrieved and logged on the application logs
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB COMPLETED! verify the results");
            var someKey = jobExecution.getExecutionContext().get("someKey");
            var top1ById = txOrderHistory.findTopByOrderByOrderIdDesc();
            System.out.println(top1ById);
        }
    }
}
