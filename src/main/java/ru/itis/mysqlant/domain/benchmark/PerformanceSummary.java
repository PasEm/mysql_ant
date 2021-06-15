package ru.itis.mysqlant.domain.benchmark;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PerformanceSummary {

    private final String user;

    private final long statements;

    private final long tableScans;

    private final double latency;

    private final long io;

    private final long ioLatency;

    private final long currentMemory;

    private final long totalMemory;
}
