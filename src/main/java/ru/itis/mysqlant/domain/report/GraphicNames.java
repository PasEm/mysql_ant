package ru.itis.mysqlant.domain.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GraphicNames {
    TPS("benchmark_tps", "TPS graphic", "Transactions per seconds"),
    LATENCY("benchmark_latency", "Latency graphic", "Latency in ms"),
    OPS("performance_schema_ops", "Database operations per second graphic by Performance Schema", "Statements per second"),
    IOS("performance_schema_ios", "Database I/Os graphic by Performance Schema", "I/Os operations per second"),
    CURRENT_MEMORY("performance_schema_current_memory", "Database current memory graphic by Performance Schema", "Current memory in KiB"),
    TOTAL_MEMORY("performance_schema_total_memory", "Database total memory graphic by Performance Schema", "Total memory in KiB"),
    TABLE_SCANS("performance_schema_no_index_usage", "Database table scan usage graphic by Performance Schema", "Table scans usage count"),
    PERFORMANCE_SCHEMA_LATENCY("performance_schema_latency", "Database latency graphic by Performance Schema", "Latency in ms");

    private final String id;
    private final String title;
    private final String metric;
}
