package ru.itis.mysqlant.service.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import ru.itis.mysqlant.configuration.BenchmarkConfiguration;
import ru.itis.mysqlant.domain.report.HandlebarsParameterMap;
import ru.itis.mysqlant.domain.report.Report;
import ru.itis.mysqlant.exception.ResultException;

import org.springframework.stereotype.Service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Jackson2Helper;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReportService {

    private final BenchmarkConfiguration configuration;
    private final TemplateLoader loader;

    private final static String TEMPLATE_FOLDER = "/reports/template";
    private final static String TEMPLATE_NAME = "template";

    public ReportService(BenchmarkConfiguration configuration) {
        this.configuration = configuration;
        this.loader = new ClassPathTemplateLoader(TEMPLATE_FOLDER, ".html");
    }

    public void createReport(List<Report> reports) {
        Handlebars handlebars = new Handlebars(loader);
        handlebars.registerHelper("json", Jackson2Helper.INSTANCE);
        HandlebarsParameterMap map = HandlebarsParameterMap.builder()
                .reports(reports)
                .mode(configuration.getRunMode().name())
                .build();
        try {
            Template template = handlebars.compile(TEMPLATE_NAME);
            String report = template.apply(map);
            String fileName = configuration.getReportPath() + File.separator + configuration.getReportName();
            Files.createDirectories(Paths.get(configuration.getReportPath()));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                writer.write(report);
            }
        } catch (IOException e) {
            log.error("Cannot save results into the file - {}", configuration.getReportName());
            throw new ResultException("Cannot save results into the file");
        }
    }
}
