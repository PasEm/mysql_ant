package ru.itis.mysqlant.util;

import ru.itis.mysqlant.domain.datasource.User;

public class UserGeneratorUtil {

    private final static String USER_NAME = "load_test_user_";
    private final static String USER_PASSWORD = "password";

    private static int count = 0;

    public static User generateUser() {
        return User.builder()
                .name(USER_NAME + count++)
                .password(USER_PASSWORD)
                .build();
    }
}
