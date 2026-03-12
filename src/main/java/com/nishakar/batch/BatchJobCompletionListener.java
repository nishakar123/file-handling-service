package com.nishakar.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Listens to Job lifecycle events and logs JobExecution details.
 */
@Slf4j
@Component
public class BatchJobCompletionListener implements JobExecutionListener {

    /**
     * Called BEFORE the job starts.
     * JobExecution is already created and persisted at this point.
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("╔══════════════════════════════════════════════╗");
        log.info("  JOB STARTING");
        log.info("  Job Name    : {}", jobExecution.getJobInstance().getJobName());
        log.info("  Execution ID: {}", jobExecution.getId());
        log.info("  Instance ID : {}", jobExecution.getJobInstance().getInstanceId());
        log.info("  Start Time  : {}", jobExecution.getStartTime());
        log.info("  Status      : {}", jobExecution.getStatus());
        log.info("  Parameters  : {}", jobExecution.getJobParameters());
        log.info("╚══════════════════════════════════════════════╝");
    }

    /**
     * Called AFTER the job completes (success, failure, or stopped).
     * Use JobExecution to inspect final status and step results.
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        // Calculate duration
        Duration duration = Duration.between(
                jobExecution.getStartTime(),
                jobExecution.getEndTime()
        );

        log.info("╔══════════════════════════════════════════════╗");
        log.info("  JOB FINISHED");
        log.info("  Job Name    : {}", jobExecution.getJobInstance().getJobName());
        log.info("  Execution ID: {}", jobExecution.getId());
        log.info("  Status      : {}", jobExecution.getStatus());
        log.info("  Exit Code   : {}", jobExecution.getExitStatus().getExitCode());
        log.info("  Duration    : {}ms", duration.toMillis());
        log.info("╚══════════════════════════════════════════════╝");

        // Step-level summary
        jobExecution.getStepExecutions().forEach(step -> {
            log.info("  └─ Step '{}' → Status: {}, Read: {}, Written: {}, Skipped: {}",
                    step.getStepName(),
                    step.getStatus(),
                    step.getReadCount(),
                    step.getWriteCount(),
                    step.getSkipCount()
            );
        });

        // Handle failures
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("  ⚠ Job FAILED. Failures:");
            jobExecution.getAllFailureExceptions()
                    .forEach(ex -> log.error("    - {}", ex.getMessage()));
        }
    }
}
