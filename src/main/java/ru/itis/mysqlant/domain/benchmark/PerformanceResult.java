package ru.itis.mysqlant.domain.benchmark;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PerformanceResult {

    private final String tableScans;

    private final String latency;

    private final String ops;

    private final String ios;

    private final String currentMemory;

    private final String totalMemory;

    private final String temporaryTables;

    private final String temporaryDiskTables;
}
