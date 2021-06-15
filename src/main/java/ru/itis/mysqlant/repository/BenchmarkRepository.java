package ru.itis.mysqlant.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import javax.sql.DataSource;

import ru.itis.mysqlant.domain.benchmark.PerformanceSummary;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Repository
public class BenchmarkRepository {

    private final static String ENABLE_SETUP_INSTRUMENTS_SQL = "UPDATE performance_schema.setup_instruments SET ENABLED = 'YES', TIMED = 'YES';";
    private final static String ENABLE_SETUP_CONSUMERS_SQL = "UPDATE performance_schema.setup_consumers SET ENABLED = 'YES';";
    private final static String DISABLE_SETUP_INSTRUMENTS_SQL = "UPDATE performance_schema.setup_instruments SET ENABLED = 'NO', TIMED = 'NO';";
    private final static String DISABLE_SETUP_CONSUMERS_SQL = "UPDATE performance_schema.setup_consumers SET ENABLED = 'NO';";
    private final static String GET_TOTAL_SUMMARY_SQL = "select user,\n" +
            "       statements,\n" +
            "       statement_latency,\n" +
            "       table_scans,\n" +
            "       file_ios,\n" +
            "       file_io_latency,\n" +
            "       current_memory,\n" +
            "       total_memory_allocated\n" +
            "from sys.`x$user_summary`\n" +
            "where user = ?;";
    private final static String GET_IO_SQL = "select * from sys.user_summary_by_file_io where user like 'load_test_user_%';";
    private final static String GET_TPS_SQL = "select * from sys.user_summary_by_statement_latency where user like 'load_test_user_%';";
    private final static String GET_INDEX_USING_SQL = "select exec_count, no_index_usage_count, no_index_usage_pct from sys.x$statements_with_full_table_scans where exec_count >= ?;";

    private final DataSource dataSource;

    public void executeScript(Statement statement, String script) {
        try {
            statement.execute(script);
        } catch (SQLException ex) {
            log.error("SQLException during the load test - {}", ex.getMessage());
        }
    }

    public void enablePerformanceSchema() {
        try (Connection connection = dataSource.getConnection()) {
            Statement enableSetupInstruments = connection.createStatement();
            Statement enableSetupConsumers = connection.createStatement();
            enableSetupInstruments.execute(ENABLE_SETUP_INSTRUMENTS_SQL);
            enableSetupConsumers.execute(ENABLE_SETUP_CONSUMERS_SQL);
        } catch (SQLException ex) {
            log.error("SQLException during the enabling the performance schema - {}", Arrays.toString(ex.getStackTrace()));
        }
    }

    public void disablePerformanceSchema() {
        try (Connection connection = dataSource.getConnection()) {
            Statement enableSetupInstruments = connection.createStatement();
            Statement enableSetupConsumers = connection.createStatement();
            enableSetupInstruments.execute(DISABLE_SETUP_INSTRUMENTS_SQL);
            enableSetupConsumers.execute(DISABLE_SETUP_CONSUMERS_SQL);
        } catch (SQLException ex) {
            log.error("SQLException during the disabling the performance schema - {}", Arrays.toString(ex.getStackTrace()));
        }
    }

    public PerformanceSummary getPerformanceSummaryByUser(String username) {
        PerformanceSummary result = null;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(GET_TOTAL_SUMMARY_SQL);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                result = PerformanceSummary.builder()
                        .user(rs.getString("user"))
                        .statements(rs.getLong("statements"))
                        .tableScans(rs.getLong("table_scans"))
                        .latency(rs.getDouble("statement_latency"))
                        .io(rs.getLong("file_ios"))
                        .ioLatency(rs.getLong("file_io_latency"))
                        .currentMemory(rs.getLong("current_memory"))
                        .totalMemory(rs.getLong("total_memory_allocated"))
                        .build();
            }
        } catch (SQLException ex) {
            log.error("SQLException during the enabling the performance schema - {}", Arrays.toString(ex.getStackTrace()));
        }
        return result;
    }
}
