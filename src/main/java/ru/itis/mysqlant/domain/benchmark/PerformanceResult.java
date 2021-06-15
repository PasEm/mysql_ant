package ru.itis.mysqlant.domain.benchmark;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PerformanceResult {

    private final long tableScans;

    private final String latency;

    private final String ops;

    private final String ios;

    private final String currentMemory;

    private final String totalMemory;
}
