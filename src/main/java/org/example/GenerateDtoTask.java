package org.example;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class GenerateDtoTask extends DefaultTask {

    @TaskAction
    public void generate() {

        Path sourceDir = getProject()
                        .getProjectDir()
                        .toPath()
                        .resolve("src/main/java");

        Path outputDir = getProject()
                        .getBuildDir()
                        .toPath()
                        .resolve("generated-dto");

        getLogger().lifecycle(
                "Generating DTO classes from: {}",
                sourceDir
        );

        try (Stream<Path> paths = Files.walk(sourceDir)) {
            paths.filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.getFileName()
                            .toString()
                            .endsWith("DTO.java"))
                    .forEach(path -> {
                        try {

                            generateDto(path, outputDir);

                        } catch (IOException e) {

                            throw new UncheckedIOException(e);
                        }
                    });

        } catch (IOException e) {
            throw new RuntimeException(
                    "Error generating DTO classes",
                    e
            );
        }
    }

    private void generateDto(
            Path path,
            Path outputDir
    ) throws IOException {
        String content = Files.readString(
                        path,
                        StandardCharsets.UTF_8
                    );

        String className = path.getFileName()
                        .toString()
                        .replace(".java", "");

        String dtoName = className + "DTO";

        Pattern fieldPattern =
                Pattern.compile(
                        "private\\s+([\\w<>?, ]+)\\s+(\\w+)\\s*;"
                );

        Matcher matcher = fieldPattern.matcher(content);

        StringBuilder fieldsBuilder = new StringBuilder();

        StringBuilder methodsBuilder = new StringBuilder();

        StringBuilder constructorBuilder = new StringBuilder();

        constructorBuilder.append("    public ")
                .append(dtoName)
                .append("() {\n")
                .append("    }\n\n");

        while (matcher.find()) {

            String type = matcher.group(1);
            String name = matcher.group(2);

            fieldsBuilder.append("    private ")
                    .append(type)
                    .append(" ")
                    .append(name)
                    .append(";\n");

            String capitalizedName =
                    Character.toUpperCase(
                            name.charAt(0)
                    ) + name.substring(1);

            methodsBuilder.append("\n")
                    .append("    public ")
                    .append(type)
                    .append(" get")
                    .append(capitalizedName)
                    .append("() {\n")
                    .append("        return ")
                    .append(name)
                    .append(";\n")
                    .append("    }\n");

            methodsBuilder.append("\n")
                    .append("    public void set")
                    .append(capitalizedName)
                    .append("(")
                    .append(type)
                    .append(" ")
                    .append(name)
                    .append(") {\n")
                    .append("        this.")
                    .append(name)
                    .append(" = ")
                    .append(name)
                    .append(";\n")
                    .append("    }\n");
        }

        String dtoContent =
                "package generated.dto;\n\n" +
                        "public class " + dtoName + " {\n\n" +
                        fieldsBuilder +
                        constructorBuilder +
                        methodsBuilder +
                        "\n}";

        Path dtoPath =
                outputDir.resolve(dtoName + ".java");

        Files.createDirectories(
                dtoPath.getParent()
        );

        Files.writeString(
                dtoPath,
                dtoContent,
                StandardCharsets.UTF_8
        );

        getLogger().lifecycle(
                "Generated DTO: {}",
                dtoPath
        );
    }
}
