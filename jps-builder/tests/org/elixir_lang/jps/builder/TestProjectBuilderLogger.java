// Copyright 2000-2025 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
// Taken from https://github.com/sh41/intellij-community/blob/8f38bff40072cba32067e2a87c6e5f640ad868e3/jps/jps-builders/testSrc/org/jetbrains/jps/builders/TestProjectBuilderLogger.java
package org.elixir_lang.jps.builder;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.impl.logging.ProjectBuilderLoggerBase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public final class TestProjectBuilderLogger extends ProjectBuilderLoggerBase {
    private final MultiMap<String, File> compiledFiles = new MultiMap<>();
    private final List<String> logLines = new ArrayList<>();

    @Override
    public void logCompiledFiles(Collection<File> files, String builderId, String description) throws IOException {
        super.logCompiledFiles(files, builderId, description);
        compiledFiles.putValues(builderId, files);
    }

    @Override
    public void logCompiledPaths(@NotNull Collection<String> paths, String builderId, String description) {
        super.logCompiledPaths(paths, builderId, description);
        compiledFiles.putValues(builderId, paths.stream().map(File::new).toList());
    }

    @Override
    public void logCompiled(@NotNull Collection<Path> files, String builderId, String description) {
        compiledFiles.putValues(builderId, files.stream().map(Path::toFile).toList());
    }

    public void clearFilesData() {
        compiledFiles.clear();
    }

  public void clearLog() {
    logLines.clear();
  }

  @NotNull
  public String getLogText() {
    return String.join("\n", logLines);
  }

  public void assertCompiled(String builderName, File[] baseDirs, String... paths) {
    assertRelativePaths(baseDirs, compiledFiles.get(builderName), paths);
  }

    private static void assertRelativePaths(File[] baseDirs, Collection<File> files, String[] expected) {
        List<String> relativePaths = new ArrayList<>();
        for (File file : files) {
            String path = file.getAbsolutePath();
            for (File baseDir : baseDirs) {
                if (baseDir != null && FileUtil.isAncestor(baseDir, file, false)) {
                    path = FileUtil.getRelativePath(baseDir, file);
                    break;
                }
            }
            assertNotNull(path);
            relativePaths.add(FileUtil.toSystemIndependentName(path));
        }
        UsefulTestCase.assertSameElements(relativePaths, expected);
    }

    @Override
    protected void logLine(String message) {
        logLines.add(message);
    }


    @Override
    public boolean isEnabled() {
        return true;
    }
}
