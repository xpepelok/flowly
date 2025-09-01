package dev.xpepelok.flowly.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@EnableAsync
@Configuration
@EnableScheduling
public class SchedulerConfiguration {
    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        System.getProperties().setProperty("jdk.virtualThreadScheduler.parallelism", "1");
        return Executors.newScheduledThreadPool(65536, Thread.ofVirtual().factory());
    }

    @Bean({"applicationTaskExecutor"})
    public AsyncTaskExecutor asyncTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}