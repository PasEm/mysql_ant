package ru.itis.mysqlant.service.benchmark;

import ru.itis.mysqlant.configuration.BenchmarkConfiguration;
import ru.itis.mysqlant.util.ParameterUtil;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class BenchmarkRunnerService {

    private final BenchmarkConfiguration benchmark;
    private final ResultService resultService;
    private final BenchmarkExecutionService executionService;
    private final ExecutorFactoryService executorFactoryService;

    public void runBenchmark() {
        log.info("Benchmark runs in the {} mode", benchmark.getRunMode().name());
        for (int clients = benchmark.getClientCount();
             clients <= benchmark.getClientMax();
             clients = ParameterUtil.getNextStepValue(
                     clients,
                     benchmark.getClientStep(),
                     benchmark.getClientMax()
             )
        ) {
            executionService.executeIntegrationTest(clients);
        }
        executorFactoryService.closeExecutorService();
        resultService.saveResults();
        log.info("Benchmark ends its work");
    }
}
