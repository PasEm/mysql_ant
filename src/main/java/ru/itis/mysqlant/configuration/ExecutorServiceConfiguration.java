package ru.itis.mysqlant.configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
public class ExecutorServiceConfiguration {

    @Bean(name = "taskExecutor")
    @ConditionalOnProperty(name = "benchmark.execution.parallel", havingValue = "true")
    public ExecutorService fixedThreadExecutor() {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(corePoolSize);
    }

    @Bean(name = "taskExecutor")
    @ConditionalOnProperty(name = "benchmark.execution.parallel", matchIfMissing = true, havingValue = "false")
    public ExecutorService singleThreadExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}
