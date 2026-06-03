package org.example;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class DtoGenerator implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        project.getTasks().register(
                "generateDto",
                GenerateDtoTask.class
        );

        project.getTasks().register(
                "cleanGeneratedDto",
                CleanDtoTask.class
        );
    }
}