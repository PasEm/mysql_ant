package ru.itis.mysqlant.service.benchmark;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import ru.itis.mysqlant.condition.TimeModeCondition;
import ru.itis.mysqlant.configuration.BenchmarkConfiguration;
import ru.itis.mysqlant.domain.datasource.User;
import ru.itis.mysqlant.service.user.UserService;
import ru.itis.mysqlant.util.ParameterUtil;
import ru.itis.mysqlant.util.TimeUtil;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
@Conditional(TimeModeCondition.class)
public class TimeBenchmarkExecutionService implements BenchmarkExecutionService {

    private final BenchmarkConfiguration benchmark;

    private final ResultService resultService;
    private final ScriptService scriptService;
    private final UserService userService;
    private final ExecutorFactoryService executorFactoryService;

    @Override
    public void executeIntegrationTest(int clients) {
        for (long duration = benchmark.getDuration();
             duration <= benchmark.getDuration();
             duration = ParameterUtil.getNextStepValue(
                     duration,
                     benchmark.getDurationStep(),
                     benchmark.getDurationMax()
             )
        ) {
            log.info("Create new test with {} clients, {} time execution in seconds", clients, TimeUtil.getSecondsFromNanoseconds(duration));
            User user = userService.createUser();
            ExecutorService executorService = executorFactoryService.createExecutorService(Runtime.getRuntime().availableProcessors());
            List<Connection> connections = new ArrayList<>();
            long latencyTime = getLatency(clients, connections);

            String resultId = resultService.createNewResult(clients, 0, duration, latencyTime, user);

            long totalTime = duration;
            AtomicInteger transactionsExceededMaxExecutionTime = new AtomicInteger(0);
            AtomicInteger totalTransactionCount = new AtomicInteger(0);
            AtomicLong currentTime = new AtomicLong(latencyTime);
            AtomicBoolean isRunning = new AtomicBoolean(true);

            List<Runnable> tasks = new ArrayList<>();
            for (int client = 0; client < clients; client++) {
                Connection connection = connections.get(client);
                Runnable task = () -> {
                    if (isRunning.get()) {
                        long executeTime = scriptService.executeScript(connection);
                        boolean isCountered = updateCurrentTime(currentTime, isRunning, executeTime, totalTime);
                        if (isCountered) {
                            updateCounter(totalTransactionCount);
                            if (executeTime > (benchmark.getTransactionMaxExecutionTime())) {
                                updateCounter(transactionsExceededMaxExecutionTime);
                            }
                        }
                    }
                };
                tasks.add(task);
            }
            while (isRunning.get()) {
                for (Runnable task : tasks) {
                    executorService.execute(task);
                }
            }
            executorFactoryService.closeExecutorService(executorService);

            log.info("Test is completed");

            resultService.updateTransactions(resultId, totalTransactionCount.get());
            resultService.updateTransactionsExceedMaxExecutionTime(resultId, transactionsExceededMaxExecutionTime.get());
            resultService.saveResult(resultId, user);

            userService.deleteUser();
        }
    }

    private boolean updateCurrentTime(AtomicLong time,
                                      AtomicBoolean isRunning,
                                      long transactionTime,
                                      long totalTime) {
        while (true) {
            long existingValue = time.get();
            long newValue = existingValue + transactionTime;

            if (newValue > totalTime) {
                isRunning.compareAndSet(isRunning.get(), false);
                return false;
            }

            if (time.compareAndSet(existingValue, newValue)) {
                return true;
            }
        }
    }

    private void updateCounter(AtomicInteger counter) {
        while (true) {
            int existingValue = counter.get();
            int newValue = existingValue + 1;
            if (counter.compareAndSet(existingValue, newValue)) {
                return;
            }
        }
    }

    private long getLatency(int clients,
                            List<Connection> connections) {
        long startLatencyTime = System.nanoTime();
        List<Connection> createdConnections = userService.getConnections(clients);
        long totalLatencyTime = System.nanoTime() - startLatencyTime;
        connections.addAll(createdConnections);
        return totalLatencyTime;
    }

}
