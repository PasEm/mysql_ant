package ru.itis.mysqlant.domain.datasource;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {

    private final String name;

    private final String password;

    public String getId() {
        return name + ":" + password;
    }
}
