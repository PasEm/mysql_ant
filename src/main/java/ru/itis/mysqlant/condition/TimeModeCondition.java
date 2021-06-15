package ru.itis.mysqlant.condition;

import java.util.Objects;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class TimeModeCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Integer transactionCount = context.getEnvironment().getProperty("benchmark.transaction.count", Integer.class);
        Long timeCount = context.getEnvironment().getProperty("benchmark.time.count", Long.class);
        return Objects.isNull(transactionCount) && Objects.nonNull(timeCount);
    }
}
