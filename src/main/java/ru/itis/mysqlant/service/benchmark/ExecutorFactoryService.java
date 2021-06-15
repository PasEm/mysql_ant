package ru.itis.mysqlant.service.benchmark;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ru.itis.mysqlant.configuration.BenchmarkConfiguration;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ExecutorFactoryService {

    private final BenchmarkConfiguration benchmark;
    private final ExecutorService executorService;

    public ExecutorService createExecutorService(int threads) {
        if (benchmark.isParallelMode()) {
            return Executors.newFixedThreadPool(threads);
        }
        return Executors.newSingleThreadExecutor();
    }

    public void closeExecutorService(ExecutorService executorService) {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
    }

    public void closeExecutorService() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
    }
}
