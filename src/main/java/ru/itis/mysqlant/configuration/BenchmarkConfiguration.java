package ru.itis.mysqlant.configuration;

import java.util.Objects;

import javax.annotation.PostConstruct;

import ru.itis.mysqlant.domain.benchmark.RunMode;
import ru.itis.mysqlant.exception.BadConfigException;
import ru.itis.mysqlant.util.TimeUtil;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.Setter;

@Setter
@Configuration
@ConfigurationProperties(prefix = "benchmark")
public class BenchmarkConfiguration {

    public final static String PREFIX = "benchmark";

    private Client client;
    private Transaction transaction;
    private Time time;
    private Script script;
    private Execution execution;
    private Report report;
    private RunMode runMode;

    @Data
    public static class Client {
        private Integer count;
        private Integer max;
        private Integer step;
    }

    @Data
    private static class Transaction {
        private Integer count;
        private Integer max;
        private Integer step;
    }

    @Data
    private static class Time {
        private Long count;
        private Long max;
        private Long step;
    }

    @Data
    private static class Script {
        private String name;
    }

    @Data
    private static class Report {
        private String path;
        private String name;
    }

    @Data
    private static class Execution {
        private Long maxTime;
        private Integer timeout;
        private Boolean parallel;
    }

    @PostConstruct
    private void postConstruct() {
        if (Objects.isNull(client) || client.getCount() < 1) {
            throw new BadConfigException("the application.yml file is invalid");
        }

        if ((Objects.isNull(time) || time.getCount() < 1)
                && (Objects.isNull(transaction) || transaction.getCount() < 1)) {
            throw new BadConfigException("the application.yml file is invalid");
        }

        if (Objects.nonNull(transaction)
                && Objects.nonNull(transaction.getCount())
                && transaction.getCount() > 0) {
            this.runMode = RunMode.TRANSACTION;
        } else {
            this.runMode = RunMode.TIME;
        }
    }

    public int getClientCount() {
        return client.getCount();
    }

    public int getClientStep() {
        return client.getStep();
    }

    public int getClientMax() {
        return client.getMax();
    }

    public int getTransactionCount() {
        return transaction.getCount();
    }

    public int getTransactionStep() {
        return transaction.getStep();
    }

    public int getTransactionMax() {
        return transaction.getMax();
    }

    public long getDuration() {
        return TimeUtil.getNanosecondsFromSeconds(time.getCount());
    }

    public long getDurationStep() {
        return TimeUtil.getNanosecondsFromSeconds(time.getStep());
    }

    public long getDurationMax() {
        return TimeUtil.getNanosecondsFromSeconds(time.getMax());
    }

    public String getScriptName() {
        return script.getName();
    }

    public String getReportPath() {
        return report.getPath();
    }

    public String getReportName() {
        return report.getName();
    }

    public long getTransactionMaxExecutionTime() {
        return TimeUtil.getNanosecondsFromMilliseconds(execution.getMaxTime());
    }

    public int getTimeoutValue() {
        return execution.getTimeout();
    }

    public boolean isParallelMode() {
        return execution.getParallel();
    }

    public RunMode getRunMode() {
        return runMode;
    }
}
