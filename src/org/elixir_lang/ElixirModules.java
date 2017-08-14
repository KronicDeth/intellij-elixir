package org.elixir_lang;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.ResourceUtil;
import com.intellij.util.io.URLUtil;
import org.elixir_lang.jps.builder.ParametersList;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ElixirModules {
    private ElixirModules() {
    }

    @NotNull
    public static ParametersList add(@NotNull ParametersList parametersList, @NotNull List<File> fileList) {
        for (File file : fileList) {
            parametersList.add("-r", file.getPath());
        }

        return parametersList;
    }

    @NotNull
    private static List<File> copy(@NotNull String basePath,
                                   @NotNull List<String> relativePathList,
                                   @NotNull String destinationDirectoryPath) throws IOException {
        List<File> destinationFileList = new ArrayList<>(relativePathList.size());

        for (String relativePath : relativePathList) {
            destinationFileList.add(copy(basePath, relativePath, destinationDirectoryPath));
        }

        return destinationFileList;
    }

    @NotNull
    private static File copy(@NotNull String basePath,
                             @NotNull String relativePath,
                             @NotNull String destinationDirectoryPath) throws IOException {
        URL moduleUrl = ResourceUtil.getResource(ElixirModules.class, basePath, relativePath);

        if (moduleUrl == null) {
            throw new IOException(
                    "Failed to locate Elixir module (under base path `" + basePath + "` on relative path `" +
                            relativePath + "`"
            );
        }

        return copy(moduleUrl, Paths.get(destinationDirectoryPath, basePath, relativePath).toFile());
    }

    @NotNull
    private static File copy(@NotNull URL moduleURL, File destination) throws IOException {
        try (BufferedInputStream inputStream = new BufferedInputStream(URLUtil.openStream(moduleURL))) {
            //noinspection ResultOfMethodCallIgnored
            destination.getParentFile().mkdirs();

            try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destination))) {
                FileUtil.copy(inputStream, outputStream);
            }
        }

        return destination;
    }

    @NotNull
    public static List<File> copy(@NotNull String basePath, @NotNull List<String> relativePathList) throws IOException {
        File temporaryDirectory = FileUtil.createTempDirectory("intellij_elixir", null);
        return copy(basePath, relativePathList, temporaryDirectory.getPath());
    }

    @NotNull
    public static ParametersList parametersList(@NotNull List<File> fileList) {
        return add(new ParametersList(), fileList);
    }
}
