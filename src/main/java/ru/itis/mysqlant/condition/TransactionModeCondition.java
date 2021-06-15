package ru.itis.mysqlant.condition;

import java.util.Objects;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class TransactionModeCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Integer transactionCount = context.getEnvironment().getProperty("benchmark.transaction.count", Integer.class);
        return Objects.nonNull(transactionCount);
    }
}
