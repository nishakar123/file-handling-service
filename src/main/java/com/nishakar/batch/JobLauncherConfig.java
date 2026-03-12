package com.nishakar.batch;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

/**
 * Provides BOTH a synchronous (default) and asynchronous JobLauncher.
 *
 * - Sync  → jobLauncher     : Controller waits for job to finish (good for short jobs)
 * - Async → asyncJobLauncher: Controller returns immediately with STARTING status
 *
 * Inject @Qualifier("asyncJobLauncher") where needed.
 */
@Configuration
public class JobLauncherConfig {

    /**
     * Synchronous JobLauncher — the default.
     * run() blocks until the job completes and returns the final JobExecution.
     */
    @Bean
    @Primary
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher launcher = new TaskExecutorJobLauncher();
        launcher.setJobRepository(jobRepository);
        // Default task executor is SyncTaskExecutor (blocking)
        launcher.afterPropertiesSet();
        return launcher;
    }

    /**
     * Asynchronous JobLauncher.
     * run() returns immediately; JobExecution status will be STARTING/STARTED.
     * Poll /batch/status/{id} to track progress.
     */
    @Bean("asyncJobLauncher")
    public JobLauncher asyncJobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher launcher = new TaskExecutorJobLauncher();
        launcher.setJobRepository(jobRepository);
        launcher.setTaskExecutor(new SimpleAsyncTaskExecutor());  // ← non-blocking
        launcher.afterPropertiesSet();
        return launcher;
    }
}
