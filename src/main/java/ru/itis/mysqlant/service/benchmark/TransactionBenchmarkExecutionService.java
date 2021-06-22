package ru.itis.mysqlant.service.benchmark;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import ru.itis.mysqlant.condition.TransactionModeCondition;
import ru.itis.mysqlant.configuration.BenchmarkConfiguration;
import ru.itis.mysqlant.domain.datasource.User;
import ru.itis.mysqlant.service.user.UserService;
import ru.itis.mysqlant.util.ParameterUtil;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
@Conditional(TransactionModeCondition.class)
public class TransactionBenchmarkExecutionService implements BenchmarkExecutionService {

    private final BenchmarkConfiguration benchmark;

    private final ResultService resultService;
    private final ScriptService scriptService;
    private final UserService userService;
    private final ExecutorFactoryService executorFactoryService;

    @Override
    public void executeIntegrationTest(int clients) {
        for (int transactions = benchmark.getTransactionCount();
             transactions <= benchmark.getTransactionMax();
             transactions = ParameterUtil.getNextStepValue(
                     transactions,
                     benchmark.getTransactionStep(),
                     benchmark.getTransactionMax()
             )
        ) {
            log.info("Create new test with {} clients, {} transactions", clients, transactions);
            User user = userService.createUser();
            ExecutorService executorService = executorFactoryService.createExecutorService(Runtime.getRuntime().availableProcessors());
            List<Connection> connections = new ArrayList<>();
            long latencyTime = getLatency(clients, connections);

            String resultId = resultService.createNewResult(clients, transactions, 0L, latencyTime, user);
            AtomicLong executionTime = new AtomicLong(latencyTime);
            AtomicInteger transactionsExceededMaxExecutionTime = new AtomicInteger(0);
            AtomicInteger transactionCounter = new AtomicInteger(0);
            int transactionCount = transactions;

            try {
                List<Runnable> tasks = new ArrayList<>();
                for (int client = 0; client < clients; client++) {
                    Connection connection = connections.get(client);
                    Runnable task = () -> {
                        if (transactionCounter.get() < transactionCount) {
                            updateCounter(transactionCounter);
                            long executeTime = scriptService.executeScript(connection);
                            updateExecutionTime(executionTime, executeTime);
                            if (executeTime > (benchmark.getTransactionMaxExecutionTime())) {
                                updateCounter(transactionsExceededMaxExecutionTime);
                            }
                        }
                    };
                    tasks.add(task);
                }

                int tasksCount = 0;
                int scriptExecutionCounter = 1;
                while (transactionCounter.get() < transactions) {
                    for (Runnable task : tasks) {
                        if (transactionCounter.get() / 50000 == scriptExecutionCounter) {
                            scriptExecutionCounter++;
                            log.info("The current count of executed scripts {}", transactionCounter.get());
                        }
                        if ((tasksCount - ExecutorFactoryService.getMaximumTasksCount(clients)) <= transactionCounter.get()) {
                            tasksCount++;
                            executorService.execute(task);
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

            executorFactoryService.closeExecutorService(executorService);

            log.info("Test is completed");

            resultService.updateExecuteTime(resultId, executionTime.get());
            resultService.updateTransactionsExceedMaxExecutionTime(resultId, transactionsExceededMaxExecutionTime.get());
            resultService.saveResult(resultId, user);

            userService.deleteUser();
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

    private void updateCounter(AtomicInteger counter) {
        while (true) {
            int existingValue = counter.get();
            int newValue = existingValue + 1;
            if (counter.compareAndSet(existingValue, newValue)) {
                return;
            }
        }
    }

    private void updateExecutionTime(AtomicLong time,
                                     long transactionTime) {
        while (true) {
            long existingValue = time.get();
            long newValue = existingValue + transactionTime;
            if (time.compareAndSet(existingValue, newValue)) {
                return;
            }
        }
    }
}
