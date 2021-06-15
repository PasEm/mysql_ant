package ru.itis.mysqlant.service.benchmark;

import java.security.SecureRandom;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

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

    private final static Random RANDOM = new SecureRandom();

    private final BenchmarkConfiguration benchmark;

    private final ResultService resultService;
    private final ScriptService scriptService;
    private final UserService userService;
    private final ExecutorService executorService;

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
            List<Connection> connections = new ArrayList<>();
            long latencyTime = getLatency(clients, connections);

            String resultId = resultService.createNewResult(clients, transactions, 0L, latencyTime, user);
            int transactionsPerUser = transactions / clients;
            int transactionsExceededMaxExecutionTime = 0;
            long totalExecutionTime = 0;

            Map<Connection, Integer> userTransactions = createUserTransactions(connections, transactionsPerUser);
            List<Callable<Long>> tasks = new ArrayList<>();

            while (!userTransactions.isEmpty()) {
                int currentClient = RANDOM.nextInt(connections.size());
                Connection connection = connections.get(currentClient);
                Callable<Long> runScriptTask = createTask(connection);
                tasks.add(runScriptTask);
                int previousValue = userTransactions.get(connection);
                if (previousValue == 1) {
                    userTransactions.remove(connection);
                    connections.remove(currentClient);
                } else {
                    userTransactions.replace(connection, --previousValue);
                }
            }

            try {
                List<Future<Long>> resultList = executorService.invokeAll(tasks);
                while (true) {
                    if (areTasksDone(resultList)) {
                        break;
                    }
                }

                log.info("Test is completed");

                for (Future<Long> result : resultList) {
                    long currentValue = result.get();
                    totalExecutionTime += currentValue;
                    if (currentValue > (benchmark.getTransactionMaxExecutionTime())) {
                        transactionsExceededMaxExecutionTime++;
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error(e.getMessage());
            }

            resultService.updateExecuteTime(resultId, totalExecutionTime);
            resultService.updateTransactionsExceedMaxExecutionTime(resultId, transactionsExceededMaxExecutionTime);
            resultService.saveResult(resultId, user);

            userService.deleteUser();
        }
    }

    private Callable<Long> createTask(Connection connection) {
        return () -> scriptService.executeScript(connection);
    }

    private Map<Connection, Integer> createUserTransactions(List<Connection> connections,
                                                            Integer transactionsPerUser) {
        Map<Connection, Integer> userTransactions = new HashMap<>();
        for (Connection connection : connections) {
            userTransactions.put(connection, transactionsPerUser);
        }
        return userTransactions;
    }

    private long getLatency(int clients,
                            List<Connection> connections) {
        long startLatencyTime = System.nanoTime();
        List<Connection> createdConnections = userService.getConnections(clients);
        long totalLatencyTime = System.nanoTime() - startLatencyTime;
        connections.addAll(createdConnections);
        return totalLatencyTime;
    }

    private boolean areTasksDone(List<Future<Long>> tasks) {
        return tasks.stream()
                .map(Future::isDone)
                .reduce(true, (a, b) -> a && b);
    }
}
