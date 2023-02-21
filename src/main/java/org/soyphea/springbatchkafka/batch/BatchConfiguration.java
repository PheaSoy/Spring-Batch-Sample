package org.soyphea.springbatchkafka.batch;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.soyphea.springbatchkafka.constants.KafkaConstant;
import org.soyphea.springbatchkafka.entity.TxOrder;
import org.soyphea.springbatchkafka.entity.TxOrderHistory;
import org.soyphea.springbatchkafka.exception.BatchRetryableException;
import org.soyphea.springbatchkafka.repository.TxOrderRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.kafka.KafkaItemReader;
import org.springframework.batch.item.kafka.KafkaItemWriter;
import org.springframework.batch.item.kafka.builder.KafkaItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;
import java.util.Properties;

@Configuration // Informs Spring that this class contains configurations
@EnableBatchProcessing // Enables batch processing for the application
public class BatchConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Lazy
    public TxOrderRepository txOrderRepository;

    @Autowired
    private KafkaTemplate<Long, TxOrderHistory> kafkaTemplate;

    @Autowired
    private KafkaProperties properties;

    @Autowired
    @Qualifier("repositoryReader")
    @Lazy
    private ItemReader itemReader;
    // Creates an instance of TxOrderProcessor that converts one data form to another. In our case the data form is maintained.
    @Bean
    public TxOrderProcessor processor() {
        return new TxOrderProcessor();
    }

    @Bean
    public TxOrderProcessorTransaction txnProcessor() {
        return new TxOrderProcessorTransaction();
    }

    // Batch jobs are built from steps. A step contains the reader, processor and the writer.
    @Bean
    public Step step1()
            throws Exception {
        return this.stepBuilderFactory
                .get("step1")
                .<TxOrder, TxOrderHistory>chunk(10)
                .reader(itemReader)
                .processor(processor())
                .writer(kafkaItemWriter())
                .faultTolerant()
                .retryLimit(10)
                .retry(BatchRetryableException.class)
                .build();
    }

    @Bean(name = "repositoryReader")
    @StepScope
    public RepositoryItemReader<TxOrder> dbReader(@Value("#{jobParameters}") Map<String,Object> jobParameters) {
        jobParameters.entrySet().forEach(e-> System.out.println(e.getKey()+"|"+e.getValue()));
        RepositoryItemReader<TxOrder> reader = new RepositoryItemReader<>();
        reader.setSort(Map.of("id", Sort.Direction.DESC));
        reader.setRepository(txOrderRepository);
        reader.setMethodName("findAll");
        return reader;
    }

    // Creates the Writer, configuring the repository and the method that will be used to save the data into the database
    @Bean
    public RepositoryItemWriter<TxOrder> dbwriter() {
        RepositoryItemWriter<TxOrder> iwriter = new RepositoryItemWriter<>();
        iwriter.setRepository(txOrderRepository);
        iwriter.setMethodName("save");
        return iwriter;
    }

//    @Bean
//    public KafkaItemReader<Long, TxOrder> kafkaItemReader() {
//        Properties props = new Properties();
//        props.putAll(this.properties.buildConsumerProperties());
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        return new KafkaItemReaderBuilder<Long, TxOrder>()
//                .partitions(0)
//                .consumerProperties(props)
//                .name("tx-order-reader")
////                .saveState(true)
//                .topic(KafkaConstant.TOPIC)
//                .build();
//    }

    @Bean
    public KafkaItemWriter<Long, TxOrderHistory> kafkaItemWriter() throws Exception {
        KafkaItemWriter<Long, TxOrderHistory> writer = new KafkaItemWriter<>();
        writer.setKafkaTemplate(kafkaTemplate);
        writer.setItemKeyMapper(TxOrderHistory::getId);
        writer.setDelete(false);
        writer.afterPropertiesSet();
        return writer;
    }

    @Bean
    public Job txnOrderJob(JobCompletionNotificationListener listener, Step step1)
            throws Exception {
        return this.jobBuilderFactory.get("txnOrderJob").
                incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step1)
                .build();
    }
}
