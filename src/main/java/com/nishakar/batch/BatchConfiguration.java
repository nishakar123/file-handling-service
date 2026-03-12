package com.nishakar.batch;

import com.nishakar.entity.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfiguration {

    private final DataSource dataSource;
    private final BatchJobCompletionListener  batchJobCompletionListener;

    @Bean
    @StepScope
    public FlatFileItemReader<Employee> reader(
            @Value("#{jobParameters['filePath']}") String filePath) {

        log.info("Reading employee from file {}", filePath);

        if (filePath == null) {
            throw new IllegalArgumentException("filePath job parameter is required");
        }

        return new FlatFileItemReaderBuilder<Employee>()
                .name("employee-csv-reader")
                .resource(new FileSystemResource(filePath))
                .linesToSkip(1)
                .delimited()
                .names("name", "age", "department", "salary", "email", "phone", "address")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Employee.class);
                }})
                .build();
    }

    @Bean
    public EmployeeItemProcessor processor(){
        return new EmployeeItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Employee> writer() {

        JdbcBatchItemWriter<Employee> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);

        writer.setSql("""
            INSERT INTO employees(name, age, department, salary, email, phone, address)
            VALUES (:name, :age, :department, :salary,  :email, :phone, :address)
        """);

        writer.setItemSqlParameterSourceProvider(
                new BeanPropertyItemSqlParameterSourceProvider<>());

        return writer;
    }

    @Bean
    public Step csvStep(JobRepository jobRepository,
                        PlatformTransactionManager transactionManager,
                        ItemReader<Employee> reader) {

        return new StepBuilder("csv-step", jobRepository)
                .<Employee, Employee>chunk(1000)
                .reader(reader)
                .processor(processor())
                .writer(writer())
                .transactionManager(transactionManager)
                .faultTolerant()
                .skipLimit(50)
                .skip(Exception.class)
                .retryLimit(3)
                .retry(Exception.class)
                .build();
    }

    @Bean
    public Job csvImportJob(JobRepository jobRepository, Step csvStep) {

        return new JobBuilder("csv-import-job", jobRepository)
                .start(csvStep)
                .listener(batchJobCompletionListener)
                .build();
    }
}
