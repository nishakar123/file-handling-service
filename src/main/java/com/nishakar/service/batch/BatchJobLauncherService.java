package com.nishakar.service.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.explore.JobExplorer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchJobLauncherService {

    private final JobLauncher jobLauncher; // synchronous by default (use AsyncJobLauncher for async)
    private final JobExplorer jobExplorer; // read-only access to batch metadata
    private final Job csvImportJob;

    // ─────────────────────────────────────────────
    // LAUNCH JOB
    // ─────────────────────────────────────────────

    public Map<String, Object> startJob(String filePath) {
        log.info("Starting BatchJobLauncherService...");
        try {
            JobParameters params =
                    new JobParametersBuilder()
                            .addString("filePath", filePath)
                            .addLong("run.id", System.currentTimeMillis())
                            .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(csvImportJob, params);
            log.info("Job Status : {} ", jobExecution.getStatus());
            return Map.of(
                    "jobExecutionId", jobExecution.getId(),
                    "jobInstanceId",  jobExecution.getJobInstance().getInstanceId(),
                    "jobName",        jobExecution.getJobInstance().getJobName(),
                    "status",         jobExecution.getStatus().toString(),
                    "exitCode",       jobExecution.getExitStatus().getExitCode(),
                    "startTime",      String.valueOf(jobExecution.getStartTime()),
                    "endTime",        String.valueOf(jobExecution.getEndTime())
            );
        } catch (Exception e) {
            log.error("Exception caught while running batch job : {}", e.getMessage(), e);
            throw new RuntimeException("Failed to start batch job", e);
        }
    }

    // ─────────────────────────────────────────────
    // QUERY JOB EXECUTION STATUS
    // ─────────────────────────────────────────────

    public Map<String, Object> getJobStatus(Long executionId) {
        JobExecution jobExecution = jobExplorer.getJobExecution(executionId);

        if (jobExecution == null) {
            return Map.of("NOT_FOUND", executionId);
        }

        // Step-level details
        List<Map<String, Object>> steps = jobExecution.getStepExecutions().stream()
                .map(step -> {
                    Map<String, Object> s = new HashMap<>();
                    s.put("stepName",    step.getStepName());
                    s.put("status",      step.getStatus().toString());
                    s.put("readCount",   step.getReadCount());
                    s.put("writeCount",  step.getWriteCount());
                    s.put("skipCount",   step.getSkipCount());
                    s.put("commitCount", step.getCommitCount());
                    s.put("exitCode",    step.getExitStatus().getExitCode());
                    return s;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("executionId",  jobExecution.getId());
        response.put("instanceId",   jobExecution.getJobInstance().getInstanceId());
        response.put("jobName",      jobExecution.getJobInstance().getJobName());
        response.put("status",       jobExecution.getStatus().toString());
        response.put("exitCode",     jobExecution.getExitStatus().getExitCode());
        response.put("startTime",    String.valueOf(jobExecution.getStartTime()));
        response.put("endTime",      String.valueOf(jobExecution.getEndTime()));
        response.put("parameters",   jobExecution.getJobParameters().toString());
        response.put("steps",        steps);

        return response;
    }

    // ─────────────────────────────────────────────
    // LIST ALL EXECUTIONS FOR A JOB
    // ─────────────────────────────────────────────

    public List<Map<String, Object>> listInstances() {
        Set<JobExecution> executions = jobExplorer.findRunningJobExecutions("csv-import-job");

        List<Map<String, Object>> result = executions.stream()
                .map(je -> Map.<String, Object>of(
                        "executionId", je.getId(),
                        "status",      je.getStatus().toString(),
                        "startTime",   String.valueOf(je.getStartTime())
                ))
                .toList();

        return result;
    }
}
