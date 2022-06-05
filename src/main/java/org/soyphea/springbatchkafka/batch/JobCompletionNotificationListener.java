package org.soyphea.springbatchkafka.batch;

import lombok.extern.slf4j.Slf4j;
import org.soyphea.springbatchkafka.repository.TxOrderRepository;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final TxOrderRepository txOrderRepository;

    public JobCompletionNotificationListener(TxOrderRepository txOrderRepository) {
        this.txOrderRepository = txOrderRepository;
    }

    // The callback method from the Spring Batch JobExecutionListenerSupport class that is executed when the batch process is completed
    @Override
    public void afterJob(JobExecution jobExecution) {
        // When the batch process is completed the the users in the database are retrieved and logged on the application logs
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB COMPLETED! verify the results");
            txOrderRepository
                    .findAll()
                    .forEach(txOrder -> log.info("tx order:{}", txOrder));
        }
    }
}
