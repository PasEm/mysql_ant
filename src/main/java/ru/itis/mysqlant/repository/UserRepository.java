package ru.itis.mysqlant.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import ru.itis.mysqlant.configuration.MySQLConfiguration;
import ru.itis.mysqlant.domain.datasource.User;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Repository
public class UserRepository {

    private final static String CREATE_USER_SQL = "CREATE USER ?@? IDENTIFIED BY ?;";
    private final static String GRANT_RIGHTS_SQL = "GRANT ALL PRIVILEGES ON %s.* TO ?@?;";
    private final static String DROP_USER_SQL = "DROP USER %s@%s;";

    private final MySQLConfiguration configuration;
    private final DataSource dataSource;

    public void createUser(User user) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement createUser = connection.prepareStatement(CREATE_USER_SQL);
            PreparedStatement grantUserRights = connection.prepareStatement(
                    String.format(GRANT_RIGHTS_SQL, configuration.getDatabaseName()));

            createUser.setString(1, user.getName());
            createUser.setString(2, configuration.getHost());
            createUser.setString(3, user.getPassword());

            grantUserRights.setString(1, user.getName());
            grantUserRights.setString(2, configuration.getHost());

            log.info("Create new test user {} in the DataBase", user.getName());
            createUser.execute();

            log.info("Grants needed rights new user");
            grantUserRights.execute();
            connection.commit();
        } catch (SQLException ex) {
            log.error("Cannot create users in the DataBase");
            throw new ru.itis.mysqlant.exception.SQLException("Cannot create users");
        }
    }

    public void deleteUser(String username) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            String query = String.format(DROP_USER_SQL, username, configuration.getHost());
            PreparedStatement dropUser = connection.prepareStatement(query);
            dropUser.execute();
            connection.commit();
        } catch (SQLException ex) {
            log.error("Cannot delete created users");
        }
    }
}
