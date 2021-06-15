package ru.itis.mysqlant.domain.report;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Graphic {

    private final String id;

    private final String title;

    private final String metricName;
}
