package ru.itis.mysqlant.service.benchmark;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ru.itis.mysqlant.configuration.BenchmarkConfiguration;
import ru.itis.mysqlant.repository.BenchmarkRepository;
import ru.itis.mysqlant.util.ScriptUtil;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScriptService {

    private final BenchmarkConfiguration configuration;

    private final BenchmarkRepository benchmarkRepository;

    private final String sqlScript;

    public ScriptService(BenchmarkConfiguration configuration,
                         ScriptUtil scriptUtil,
                         BenchmarkRepository benchmarkRepository) {
        this.configuration = configuration;
        this.benchmarkRepository = benchmarkRepository;
        this.sqlScript = scriptUtil.loadSQLScript();
    }

    public long executeScript(Connection connection) {
        long scriptExecuteTime = 0L;
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(configuration.getTimeoutValue());
            long startExecuteTime = System.nanoTime();
            benchmarkRepository.executeScript(statement, sqlScript);
            connection.commit();
            scriptExecuteTime = System.nanoTime() - startExecuteTime;
        } catch (SQLException ex) {
            log.error(Arrays.toString(ex.getStackTrace()));
        }
        return scriptExecuteTime;
    }

    public List<String> getDigestListBySQLStatements() {
        List<String> statements = Arrays.stream(sqlScript.split(";"))
                .collect(Collectors.toList());
        return benchmarkRepository.getStatementDigest(statements);
    }
}
