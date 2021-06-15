package ru.itis.mysqlant.handler;

import java.io.IOException;

import ru.itis.mysqlant.exception.SQLException;
import ru.itis.mysqlant.repository.BenchmarkRepository;
import ru.itis.mysqlant.service.user.UserService;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@ControllerAdvice
public class ExceptionServiceHandler {

    private final UserService userService;
    private final BenchmarkRepository benchmarkRepository;

    //UnsatisfiedDependencyException

    @ExceptionHandler({IOException.class, Exception.class})
    public void globalExceptionHandler(Exception ex) {
        log.error("Error during the benchmark work - {}", ex.getMessage());
        restoreOriginDataBaseState();
    }

    @ExceptionHandler(SQLException.class)
    public void sqlExceptionHandler(Exception ex) {
        log.error("SQL error - {}", ex.getMessage());
        restoreOriginDataBaseState();
    }

    private void restoreOriginDataBaseState() {
        log.info("Trying to delete all created users");
        userService.deleteUser();
        log.info("Trying to disable performance schema");
        benchmarkRepository.disablePerformanceSchema();
        log.info("Benchmark is emergency terminated");
    }

}
