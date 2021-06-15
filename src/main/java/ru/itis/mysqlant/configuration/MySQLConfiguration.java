package ru.itis.mysqlant.configuration;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "mysql")
public class MySQLConfiguration {

    public final static String PREFIX = "mysql";

    private final static String HOST_PREFIX = "://";
    private final static String HOST_SUFFIX = ":";
    private final static String DATABASE_NAME_PREFIX = "/";

    private String username;

    private String password;

    private String driverName;

    private String url;

    public String getHost() {
        int startIndex = url.indexOf(HOST_PREFIX);
        int endIndex = url.indexOf(HOST_SUFFIX, startIndex + 1);
        return url.substring(startIndex + HOST_PREFIX.length(), endIndex);
    }

    public String getDatabaseName() {
        int startIndex = url.lastIndexOf(DATABASE_NAME_PREFIX);
        return url.substring(startIndex + 1);
    }

    @Bean
    public DataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverName);
        config.setPoolName("Benchmark_Setup_Pool");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }
}
