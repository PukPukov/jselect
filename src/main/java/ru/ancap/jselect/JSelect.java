package ru.ancap.jselect;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringSubstitutor;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class JSelect {
    
    public static final String V_TAG = "v";
    public static final String JAVA_PATH_TAG = "java_path";
    
    public static final String RESULT_FOLDER = "jselect-shortcuts";
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    // logic
    
    public static void main(String[] args) throws Exception {
        Path rootDir = directory();
        Path runnablesDir = rootDir.resolve(RESULT_FOLDER);
        
        List<Template> templates = loadTemplates();
        
        Map<String, String> javas = config(rootDir).get("javas");
        Files.createDirectories(runnablesDir);
        int generated = generateScripts(templates, javas, runnablesDir);
        
        System.out.printf("Generated "+generated+" shortcuts");
    }
    
    @SneakyThrows
    private static int generateScripts(
        List<Template> templates,
        Map<String, String> javaList,
        Path outputDir
    ) {
        int counter = 0;
        for (Template template : templates) {
            for (Map.Entry<String, String> entry : javaList.entrySet()) {
                var replacements = Map.of(
                    V_TAG, entry.getKey(),
                    JAVA_PATH_TAG, entry.getValue()
                );
                var substitutor = new StringSubstitutor(replacements);
                String fileName = substitutor.replace(template.baseName);
                String content = substitutor.replace(template.content);
                Files.writeString(outputDir.resolve(fileName), content);
                counter++;
            }
        }
        return counter;
    }
    
    @SneakyThrows
    private static List<Template> loadTemplates() {
        List<String> paths = listResource("/templates/templates-enumeration.json"); // additional line for type clarification
        return paths.stream()
            .map(name -> new Template(
                FilenameUtils.removeExtension(name),
                stringResource("/templates/" + name)
            ))
            .toList();
    }
    
    // shit
    
    @SneakyThrows
    private static List<String> listResource(String path) {
        return mapper.readValue(
            JSelect.class.getResourceAsStream(path),
            new TypeReference<>() {}
        );
    }
    
    @SneakyThrows
    private static String stringResource(String path) {
        try (InputStream inputStream = JSelect.class.getResourceAsStream(path)) {
            assert inputStream != null;
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }
    
    private static Map<String, Map<String, String>> config(Path rootDir) throws IOException { /* Map<String, String> can be used as a key as a shortcut since we have only one 
        entry in the config, can be changed later */
        Path userConfig = rootDir.resolve("configuration.json");
        if (Files.exists(userConfig)) {
            return mapper.readValue(userConfig.toFile(), new TypeReference<>() {});
        } else {
            return mapper.readValue(
                JSelect.class.getResourceAsStream("/configuration.json"),
                new TypeReference<>() {}
            );
        }
    }
    
    private static Path directory() throws URISyntaxException {
        return Paths.get(JSelect.class.getProtectionDomain()
            .getCodeSource().getLocation().toURI()).getParent();
    }
    
    record Template(String baseName, String content) {}
    
}