package org.soyphea.springbatchkafka;

import lombok.extern.slf4j.Slf4j;
import org.soyphea.springbatchkafka.constants.KafkaConstant;
import org.soyphea.springbatchkafka.entity.TxOrder;
import org.soyphea.springbatchkafka.repository.TxOrderRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class SpringBatchKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchKafkaApplication.class, args);
    }

    @Autowired
    private KafkaTemplate<Long, TxOrder> kafkaTemplate;

    @Autowired
    private TxOrderRepository txOrderRepository;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("txnOrderJob")
    private Job job;

    //    @Scheduled(fixedRate = 2000)
    public void produce() {
        txOrderRepository
                .findByStatus("created")
                .forEach(tx -> {
                    log.info("Schedule send order with id:{}", tx.getId());
                    kafkaTemplate.send(KafkaConstant.TOPIC, System.currentTimeMillis(), tx);
                });
    }

    @Scheduled(fixedRate = 5000)
    public void executeJob() {
        JobParameters Parameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis()).toJobParameters();
        try {
            jobLauncher.run(job, Parameters);
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException | JobParametersInvalidException | JobRestartException e) {

            e.printStackTrace();
        }
    }

////	@Bean
//	ApplicationRunner applicationRunner(TxOrderRepository txOrderRepository){
//		return agr -> {
//			IntStream.range(1,10000).forEach(
//					i -> {
//						TxOrder txOrder = TxOrder.builder()
//								.name("test"+i)
//								.amount(1+Double.parseDouble(""+i))
//								.status("created")
//								.remark("test loop")
//								.build();
//						txOrderRepository.save(txOrder);
//					}
//			);
//
//		};
//	}

}
