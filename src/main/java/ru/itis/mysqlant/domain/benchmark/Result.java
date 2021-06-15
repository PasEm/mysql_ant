package ru.itis.mysqlant.domain.benchmark;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Result {

    private int clients;

    private int transactions;

    private int transactionsExceededMaxExecutionTime;

    private long executeTime;

    private long latency;

    private PerformanceResult performanceResult;

    public String getId() {
        return clients + "-" + transactions;
    }

    public double getTps(long operations) {
        return operations / (executeTime / 1_000_000_000.0);
    }
}
