package ru.itis.mysqlant.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import ru.itis.mysqlant.configuration.BenchmarkConfiguration;
import ru.itis.mysqlant.exception.InvalidSQLScriptException;
import ru.itis.mysqlant.exception.SQLScriptNotFoundException;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class ScriptUtil {

    private final BenchmarkConfiguration configuration;

    public String loadSQLScript() {
        String path = configuration.getScriptName();
        StringBuilder input = new StringBuilder();

        try (
                BufferedReader reader = new BufferedReader(new FileReader(path))
        ) {
            log.info("Load SQL script from the file {}", path);
            while (reader.ready()) {
                input.append(reader.readLine());
            }
        } catch (FileNotFoundException e) {
            throw new SQLScriptNotFoundException("Script not found");
        } catch (IOException e) {
            throw new InvalidSQLScriptException("Script is not valid");
        }

        return input.toString();
    }
}
