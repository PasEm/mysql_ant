package ru.itis.mysqlant.application;

import ru.itis.mysqlant.service.benchmark.BenchmarkRunnerService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan("ru.itis.mysqlant")
public class MySQLLoadTestApplication implements CommandLineRunner {

    private final BenchmarkRunnerService benchmarkRunnerService;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MySQLLoadTestApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }

    @Override
    public void run(String... args) {
        benchmarkRunnerService.runBenchmark();
    }
}
