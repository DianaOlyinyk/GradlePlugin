package org.example;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

public abstract class CleanDtoTask extends DefaultTask {

    @TaskAction
    public void clean() {

        File dir = new File(
                        getProject().getBuildDir(),
                        "generated-dto");

        if (dir.exists()) {
            getProject().delete(dir);
            getLogger().lifecycle("Generated DTO deleted"
            );
        }
    }
}