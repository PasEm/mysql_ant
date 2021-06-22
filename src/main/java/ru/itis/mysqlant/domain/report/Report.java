package ru.itis.mysqlant.domain.report;

import ru.itis.mysqlant.domain.benchmark.PerformanceResult;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Report {

    private final String clients;

    private final String transactions;

    private final String transactionsExceededMaxExecuteTime;

    private final String tps;

    private final String latency;

    private final String time;

    private final PerformanceResult performanceResult;

}
