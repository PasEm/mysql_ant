package ru.itis.mysqlant.service.benchmark;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import ru.itis.mysqlant.configuration.BenchmarkConfiguration;
import ru.itis.mysqlant.repository.BenchmarkRepository;
import ru.itis.mysqlant.util.ScriptUtil;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScriptService {

    private final BenchmarkConfiguration configuration;
    private final BenchmarkRepository repository;

    private final String SQL_SCRIPT;

    public ScriptService(BenchmarkConfiguration configuration,
                         ScriptUtil scriptUtil,
                         BenchmarkRepository repository) {
        this.configuration = configuration;
        this.repository = repository;
        this.SQL_SCRIPT = scriptUtil.loadSQLScript();
    }

    public long executeScript(Connection connection) {
        long scriptExecuteTime = 0L;
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(configuration.getTimeoutValue());
            long startExecuteTime = System.nanoTime();
            repository.executeScript(statement, SQL_SCRIPT);
            connection.commit();
            scriptExecuteTime = System.nanoTime() - startExecuteTime;
        } catch (SQLException ex) {
            log.error(Arrays.toString(ex.getStackTrace()));
        }
        return scriptExecuteTime;
    }
}
