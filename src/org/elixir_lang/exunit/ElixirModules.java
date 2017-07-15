package org.elixir_lang.exunit;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.ResourceUtil;
import com.intellij.util.io.URLUtil;
import org.elixir_lang.sdk.ElixirSdkRelease;
import org.elixir_lang.sdk.ElixirSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;

public class ElixirModules {
    private static final String FORMATTER_FILE_NAME = "team_city_ex_unit_formatter.ex";
    private static final String FORMATTING_FILE_NAME = "team_city_ex_unit_formatting.ex";
    private static final String MIX_TASK_FILE_NAME = "test_with_formatter.ex";

    private ElixirModules() {
    }

    private static File putFile(@NotNull String relativePath, @NotNull File directory) throws IOException {
        URL moduleUrl = ResourceUtil.getResource(ElixirModules.class, "/exunit", relativePath);

        if (moduleUrl == null) {
            throw new IOException("Failed to locate Elixir module " + relativePath);
        }

        try (BufferedInputStream inputStream = new BufferedInputStream(URLUtil.openStream(moduleUrl))) {
            File file = new File(directory, relativePath);
            file.getParentFile().mkdirs();

            try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
                FileUtil.copy(inputStream, outputStream);
                return file;
            }
        }
    }

    public static File putFormatterTo(@NotNull File directory, @Nullable Project project) throws IOException {
        Sdk sdk = null;

        if (project != null) {
            sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        }

        return putFormatterTo(directory, sdk);
    }

    private static File putFormatterTo(@NotNull File directory, @Nullable Sdk sdk) throws IOException {
        String versionDirectory = "1.4.0";

        ElixirSdkRelease release = ElixirSdkType.getRelease(sdk);

        if (release != null && release.compareTo(ElixirSdkRelease.V_1_4) < 0) {
            versionDirectory = "1.1.0";
        }

        String source = versionDirectory + "/" + FORMATTER_FILE_NAME;
        return putFile(source, directory);
    }

    public static File putFormattingTo(@NotNull File directory) throws IOException {
        return putFile(FORMATTING_FILE_NAME, directory);
    }

    public static File putMixTaskTo(@NotNull File directory) throws IOException {
        return putFile(MIX_TASK_FILE_NAME, directory);
    }
}
