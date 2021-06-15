package ru.itis.mysqlant.domain.report;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HandlebarsParameterMap {

    private final String mode;

    private final List<Report> reports;
}
