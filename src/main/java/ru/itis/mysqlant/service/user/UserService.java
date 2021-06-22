package ru.itis.mysqlant.service.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.itis.mysqlant.configuration.MySQLConfiguration;
import ru.itis.mysqlant.domain.datasource.User;
import ru.itis.mysqlant.repository.UserRepository;
import ru.itis.mysqlant.util.UserGeneratorUtil;

import org.springframework.stereotype.Service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    private final MySQLConfiguration mySQLConf;
    private final UserRepository repository;

    private final List<Connection> connections;

    private User user;
    private HikariDataSource userDatasource;

    public UserService(MySQLConfiguration mySQLConf,
                       UserRepository repository) {
        this.mySQLConf = mySQLConf;
        this.repository = repository;
        this.connections = new ArrayList<>();
    }

    public User createUser() {
        user = UserGeneratorUtil.generateUser();
        repository.createUser(user);
        return user;
    }

    public List<Connection> getConnections(Integer userCount) {
        try {
            userDatasource = createUserDataSource(userCount);
            for (int i = 0; i < userCount; i++) {
                Connection connection = userDatasource.getConnection();
                connection.setAutoCommit(false);
                connections.add(connection);
            }
            return connections;
        } catch (SQLException ex) {
            throw new ru.itis.mysqlant.exception.SQLException("Can`t connect to database");
        }
    }

    public void deleteUser() {
        closeUserConnections();
        userDatasource.close();
        repository.deleteUser(user.getName());
    }

    private void closeUserConnections() {
        for (Connection connection : connections) {
            try {
                connection.close();
            } catch (SQLException ex) {
                log.error("Exception during close connection: {}", Arrays.toString(ex.getStackTrace()));
            }
        }
        connections.clear();
    }

    private HikariDataSource createUserDataSource(int connections) {
        String url = mySQLConf.getUrl().contains("?") ?
                mySQLConf.getUrl().concat("&allowMultiQueries=true") :
                mySQLConf.getUrl().concat("?allowMultiQueries=true");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user.getName());
        config.setPassword(user.getPassword());
        config.setDriverClassName(mySQLConf.getDriverName());
        config.setMaximumPoolSize(connections);
        config.setPoolName("MYSQL_ANT_POOL");
        config.setMinimumIdle(connections);
        config.setAutoCommit(false);
        config.setTransactionIsolation("TRANSACTION_READ_UNCOMMITTED");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }

}
