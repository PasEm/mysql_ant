package ru.itis.mysqlant.service.benchmark;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import ru.itis.mysqlant.domain.benchmark.PerformanceResult;
import ru.itis.mysqlant.domain.benchmark.PerformanceSummary;
import ru.itis.mysqlant.domain.benchmark.Result;
import ru.itis.mysqlant.domain.datasource.User;
import ru.itis.mysqlant.domain.report.Report;
import ru.itis.mysqlant.repository.BenchmarkRepository;
import ru.itis.mysqlant.service.report.ReportService;
import ru.itis.mysqlant.util.MemoryUtil;
import ru.itis.mysqlant.util.StringUtil;
import ru.itis.mysqlant.util.TimeUtil;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResultService {

    private final ReportService reportService;
    private final BenchmarkRepository repository;

    private final Map<String, Result> results;
    private final Map<String, PerformanceSummary> performanceMap;

    public ResultService(ReportService reportService,
                         BenchmarkRepository repository) {
        this.reportService = reportService;
        this.repository = repository;
        this.results = new LinkedHashMap<>();
        this.performanceMap = new HashMap<>();

        this.repository.enablePerformanceSchema();
    }

    @PreDestroy
    private void disablePerformanceSchema() {
        repository.disablePerformanceSchema();
    }

    public String createNewResult(int clients,
                                  int transactions,
                                  long executeTime,
                                  long latency,
                                  User user) {
        Result result = Result.builder()
                .clients(clients)
                .transactions(transactions)
                .latency(latency)
                .executeTime(executeTime)
                .transactionsExceededMaxExecutionTime(0)
                .performanceResult(null)
                .build();
        results.put(result.getId(), result);
        performanceMap.put(result.getId(), repository.getPerformanceSummaryByUser(user.getName()));
        return result.getId();
    }

    public void updateTransactions(String resultId, int transactions) {
        Result result = results.get(resultId);

        result.setTransactions(transactions);
        results.replace(resultId, result);
    }

    public void updateExecuteTime(String resultId, long executeTime) {
        Result result = results.get(resultId);

        result.setExecuteTime(result.getExecuteTime() + executeTime);
        results.replace(resultId, result);
    }

    public void updateTransactionsExceedMaxExecutionTime(String resultId,
                                                         int transactions) {
        Result result = results.get(resultId);

        result.setTransactionsExceededMaxExecutionTime(transactions);
        results.replace(resultId, result);
    }

    public void saveResult(String resultId, User user) {
        Result result = results.get(resultId);
        PerformanceSummary afterTest = repository.getPerformanceSummaryByUser(user.getName());
        PerformanceSummary beforeTest = performanceMap.get(resultId);

        long statements = afterTest.getStatements() - beforeTest.getStatements();
        long tableScans = afterTest.getTableScans() - beforeTest.getTableScans();
        long currentMemory = afterTest.getCurrentMemory();
        long totalMemory = afterTest.getTotalMemory();
        double ios = afterTest.getIo() - beforeTest.getIo();
        double latency = afterTest.getLatency() - beforeTest.getLatency();
        double ioLatency = afterTest.getIoLatency() - beforeTest.getIoLatency();

        latency = statements == 0 ? 0 : latency / statements;
        ios = TimeUtil.getMilliseconds(ioLatency) == 0 ? 0 : ios / TimeUtil.getMilliseconds(ioLatency);

        PerformanceResult performanceResult = PerformanceResult.builder()
                .ops(StringUtil.getFormattedTps(result.getTps(statements)))
                .tableScans(tableScans)
                .latency(StringUtil.getFormattedTimeInMillis(
                        TimeUtil.getMilliseconds(latency))
                )
                .ios(StringUtil.getFormattedNumber(ios))
                .currentMemory(StringUtil.getFormattedNumber(
                        MemoryUtil.getKilobytes(currentMemory))
                )
                .totalMemory(StringUtil.getFormattedNumber(
                        MemoryUtil.getKilobytes(totalMemory))
                )
                .build();

        result.setPerformanceResult(performanceResult);
    }

    public void saveResults() {
        List<Report> reports = results.values()
                .stream()
                .map(result -> Report.builder()
                        .clients(result.getClients())
                        .tps(StringUtil.getFormattedTps(result.getTps(result.getTransactions())))
                        .time(StringUtil.getFormattedTimeInSeconds(result.getExecuteTime()))
                        .latency(StringUtil.getFormattedTimeInSeconds(result.getLatency()))
                        .transactions(result.getTransactions())
                        .transactionsExceededMaxExecuteTime(result
                                .getTransactionsExceededMaxExecutionTime()
                        )
                        .performanceResult(result.getPerformanceResult())
                        .build()
                )
                .collect(Collectors.toList());
        reportService.createReport(reports);
    }
}
