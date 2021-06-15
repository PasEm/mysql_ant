package ru.itis.mysqlant.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import ru.itis.mysqlant.configuration.BenchmarkConfiguration;
import ru.itis.mysqlant.configuration.MySQLConfiguration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class LoadPropertyUtil {

    private final static String PATH_TO_PROPERTY = "src/main/resources";

    private final static String PROPERTY_FILE_NAME = "application.yml";

    private final ObjectMapper mapper;

    private final Map<String, Object> params;

    public LoadPropertyUtil() throws IOException {
        this.mapper = new ObjectMapper(new YAMLFactory());
        this.params = mapper.readValue(new File(getPropertyFilePath()), new TypeReference<>() {});
    }

    public BenchmarkConfiguration readBenchmarkConfiguration(String prefix) {
        return mapper.convertValue(params.get(prefix), BenchmarkConfiguration.class);
    }

    public MySQLConfiguration readMySQLConfiguration(String prefix) {
        return mapper.convertValue(params.get(prefix), MySQLConfiguration.class);
    }

    private String getPropertyFilePath() {
        return PATH_TO_PROPERTY.concat(File.separator).concat(PROPERTY_FILE_NAME);
    }
}
