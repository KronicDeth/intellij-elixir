package org.elixir_lang.jps.builder;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.PathUtilRt;
import org.elixir_lang.jps.builder.compiler_options.Extension;
import org.elixir_lang.jps.builder.model.SdkProperties;
import org.elixir_lang.jps.builder.sdk_type.Elixir;
import org.elixir_lang.jps.builder.sdk_type.Erlang;
import org.elixir_lang.jps.shared.CompilerOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.library.JpsLibrary;
import org.jetbrains.jps.model.library.JpsLibraryRoot;
import org.jetbrains.jps.model.library.JpsOrderRootType;
import org.jetbrains.jps.model.library.JpsTypedLibrary;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.util.JpsPathUtil;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;


/**
 * Created by zyuyou on 15/7/17.
 */
public class BuilderTest extends JpsBuildTestCase {
    private static final String TEST_MODULE_NAME = "m";
    private JpsSdk<SdkProperties> elixirSdk;
    private static String otpReleaseVersion = null;


    /**
     * Executes Erlang code by writing it to a temp escript file.
     * This avoids command-line parsing issues on Windows.
     */
    private static String executeErlangCode(String erlangCode) throws IOException, InterruptedException {
        File tempScript = Files.createTempFile("erl_exec_", ".erl").toFile();
        tempScript.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempScript)) {
            writer.write("#!/usr/bin/env escript\n");
            writer.write("%%! -noshell\n");
            writer.write("main(_) -> " + erlangCode + "\n");
        }

        String escriptCmd = System.getProperty("os.name").toLowerCase().contains("windows") ? "escript.exe" : "escript";
        ProcessBuilder pb = new ProcessBuilder(escriptCmd, tempScript.getAbsolutePath());
        Process process = pb.start();

        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stdout.append(line).append("\n");
            }
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stderr.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        //noinspection ResultOfMethodCallIgnored
        tempScript.delete();

        if (exitCode != 0) {
            throw new RuntimeException("Erlang execution failed. Exit code: " + exitCode + "\nSTDERR: " + stderr);
        }

        return stdout.toString().trim();
    }

    /**
     * Retrieves the OTP Release version, eg 24.3.4.6
     * This method first checks for an environment variable OTP_RELEASE (used in CI).
     * If not set, it attempts to infer the version path from the erl command.
     *
     * @return A String representing the OTP Release version.
     * @throws AssertionError if ERLANG_SDK_HOME is not set and cannot be inferred from PATH.
     */
    private static String otpRelease() {
        if (otpReleaseVersion != null) {
            return otpReleaseVersion;
        }

        String otpRelease = System.getenv("OTP_RELEASE");
        if (otpRelease != null && !otpRelease.isEmpty()) {
            otpReleaseVersion = otpRelease;
            return otpRelease;
        }

        try {
            otpRelease = executeErlangCode(
                    "{ok, Version} = file:read_file(filename:join([code:root_dir(), \"releases\", erlang:system_info(otp_release), \"OTP_VERSION\"])), io:fwrite(Version), halt()."
            );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get OTP release", e);
        }

        if (otpRelease.isEmpty()) {
            throw new RuntimeException("Failed to get OTP release");
        }

        otpReleaseVersion = otpRelease;
        return otpRelease;
    }

    @NotNull
    private static String elixirSdkHome() {
        return sdkHomeFromEbinDirectory(ebinDirectory());
    }

    /**
     * Retrieves the Erlang SDK home directory.
     * This method first checks for an environment variable ERLANG_SDK_HOME.
     * If not set, it attempts to infer the SDK home from the erl command.
     *
     * @return A String representing the path to the Erlang SDK home directory.
     * @throws AssertionError if ERLANG_SDK_HOME is not set and cannot be retrieved from the erl command.
     */
    @NotNull
    private static String erlangSdkHome() {
        String erlangSdkHome = System.getenv("ERLANG_SDK_HOME");

        if (erlangSdkHome != null && !erlangSdkHome.isEmpty()) {
            return erlangSdkHome;
        }

        try {
            erlangSdkHome = executeErlangCode("io:format(\"~s~n\", [code:root_dir()]), halt().");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get Erlang SDK home", e);
        }

        if (erlangSdkHome.isEmpty()) {
            throw new RuntimeException("Failed to get Erlang SDK home");
        }

        return erlangSdkHome;
    }

    @NotNull
    private static String ebinDirectory() {
        String ebinDirectory = System.getenv("ELIXIR_EBIN_DIRECTORY");

        assertNotNull("ELIXIR_EBIN_DIRECTORY is not set", ebinDirectory);

        return ebinDirectory;
    }

    @NotNull
    private static String sdkHomeFromEbinDirectory(@NotNull String ebinDirectory) {
        return new File(ebinDirectory)
                .getParentFile()
                .getParentFile()
                .getParentFile()
                .toString();
    }

    private static String elixirVersion() {
        String elixirVersion = System.getenv("ELIXIR_VERSION");

        assertNotNull("ELIXIR_VERSION is not set", elixirVersion);

        return elixirVersion;
    }

    public void testElixirc() {
        CompilerOptions compilerOptions = Extension.getOrCreateExtension(myModel.getProject()).getOptions();
        compilerOptions.useMixCompiler = false;

        String depFile = createFile("lib/simple.ex", "defmodule Simple do def foo() do :ok end end");
        addModule(
                TEST_MODULE_NAME,
                new String[]{PathUtilRt.getParentPath(depFile)},
                getAbsolutePath("_build/dev"),
                getAbsolutePath("_build/test"),
                elixirSdk
        );
        BuildResult rebuildResult = doBuild(CompileScopeTestBuilder.rebuild().all());
        assertSuccessfulWithLogs(rebuildResult);
        assertCompiled("lib/simple.ex");
        String absolutePath = getAbsolutePath("_build/dev/");
        assertNotNull(FileUtil.findFileInProvidedPath(absolutePath,"Elixir.Simple.beam"));

        BuildResult makeResult = doBuild(CompileScopeTestBuilder.make().all());
        assertSuccessfulWithLogs(makeResult);
        makeResult.assertUpToDate();
        checkMappingsAreSameAfterRebuild(makeResult);
    }

    public void testMix() throws IOException {
        CompilerOptions compilerOptions = Extension.getOrCreateExtension(myModel.getProject()).getOptions();
        compilerOptions.useMixCompiler = true;
        // TODO: renenable
        //compilerOptions.useMixToolingBootstrap = false;

        setupMixProject();
        BuildResult result = doBuild(CompileScopeTestBuilder.rebuild().all());
        assertSuccessfulWithLogs(result);
        assertCompiled(mixCompiledPaths());
        assertMixCompiled();

        BuildResult makeResult = doBuild(CompileScopeTestBuilder.make().all());
        assertSuccessfulWithLogs(makeResult);
        makeResult.assertUpToDate();
        checkMappingsAreSameAfterRebuild(makeResult);
    }

    public void testElixircWarningsAsErrors() {
        CompilerOptions compilerOptions = Extension.getOrCreateExtension(myModel.getProject()).getOptions();
        compilerOptions.useMixCompiler = false;
        compilerOptions.warningsAsErrorsEnabled = false;

        String depFile = createFile("lib/warn.ex", warningModuleSource());
        addModule(
                TEST_MODULE_NAME,
                new String[]{PathUtilRt.getParentPath(depFile)},
                getAbsolutePath("_build/dev"),
                getAbsolutePath("_build/test"),
                elixirSdk
        );
        BuildResult warningResult = doBuild(CompileScopeTestBuilder.rebuild().all());
        assertSuccessfulWithLogs(warningResult);
        assertTrue("Expected warnings from unused variable", hasWarningMessage(warningResult));

        compilerOptions.warningsAsErrorsEnabled = true;
        change(depFile);
        BuildResult errorResult = doBuild(CompileScopeTestBuilder.make().all());
        errorResult.assertFailed();
    }

    public void testMixWarningsAsErrors() throws IOException {
        CompilerOptions compilerOptions = Extension.getOrCreateExtension(myModel.getProject()).getOptions();
        compilerOptions.useMixCompiler = true;
        // TODO: renenable
        // compilerOptions.useMixToolingBootstrap = false;
        compilerOptions.warningsAsErrorsEnabled = false;

        setupMixProject();
        String warnFile = createFile("lib/warn.ex", warningModuleSource());
        BuildResult warningResult = doBuild(CompileScopeTestBuilder.rebuild().all());
        assertSuccessfulWithLogs(warningResult);
        assertTrue("Expected warnings from unused variable", hasWarningMessage(warningResult));

        compilerOptions.warningsAsErrorsEnabled = true;
        change(warnFile);
        BuildResult errorResult = doBuild(CompileScopeTestBuilder.make().all());
        errorResult.assertFailed();
    }

    public void testElixircDeletesSource() {
        CompilerOptions compilerOptions = Extension.getOrCreateExtension(myModel.getProject()).getOptions();
        compilerOptions.useMixCompiler = false;

        String depFile = createFile("lib/delete_me.ex", "defmodule DeleteMe do def foo() do :ok end end");
        addModule(
                TEST_MODULE_NAME,
                new String[]{PathUtilRt.getParentPath(depFile)},
                getAbsolutePath("_build/dev"),
                getAbsolutePath("_build/test"),
                elixirSdk
        );
        BuildResult rebuildResult = doBuild(CompileScopeTestBuilder.rebuild().all());
        assertSuccessfulWithLogs(rebuildResult);

        File deleteFile = new File(depFile);
        assertTrue("Failed to delete " + depFile, FileUtil.delete(deleteFile));

        BuildResult makeResult = doBuild(CompileScopeTestBuilder.make().all());
        assertSuccessfulWithLogs(makeResult);
        checkMappingsAreSameAfterRebuild(makeResult);
    }

    public void testMixDeletesSource() throws IOException {
        CompilerOptions compilerOptions = Extension.getOrCreateExtension(myModel.getProject()).getOptions();
        compilerOptions.useMixCompiler = true;
        // TODO: renenable
        // compilerOptions.useMixToolingBootstrap = false;

        setupMixProject();
        BuildResult rebuildResult = doBuild(CompileScopeTestBuilder.rebuild().all());
        assertSuccessfulWithLogs(rebuildResult);

        File deleteFile = new File(getAbsolutePath("lib/mix_compiled.ex"));
        assertTrue("Failed to delete " + deleteFile, FileUtil.delete(deleteFile));

        BuildResult makeResult = doBuild(CompileScopeTestBuilder.make().all());
        assertSuccessfulWithLogs(makeResult);
        checkMappingsAreSameAfterRebuild(makeResult);
    }

    private void setupMixProject() throws IOException {
        FileUtil.copyDirContent(new File("testData/mix_compiled"), getOrCreateProjectDir());
        addModule(
                "mix_compiled",
                new String[]{getOrCreateProjectDir().getAbsolutePath()},
                getAbsolutePath("_build/dev"),
                getAbsolutePath("_build/test"),
                elixirSdk
        );
    }

    private void assertMixCompiled() {
        String absolutePath = getAbsolutePath("_build/dev/lib/mix_compiled/ebin/");
        assertNotNull(FileUtil.findFileInProvidedPath(absolutePath, "Elixir.MixCompiled.beam"));
    }

    private static String[] mixCompiledPaths() {
        return new String[] {
                "lib/mix_compiled.ex"
        };
    }

    private static boolean hasWarningMessage(BuildResult result) {
        if (!result.getMessages(BuildMessage.Kind.WARNING).isEmpty()) {
            return true;
        }

        for (BuildMessage message : result.getMessages(BuildMessage.Kind.INFO)) {
            if (message.getMessageText().contains("warning:")) {
                return true;
            }
        }
        return false;
    }

    private static String warningModuleSource() {
        return """
                defmodule Warn do
                  require Logger
                  def foo do
                    unused = 1
                    :ok
                  end
                end
                """;
    }

    private JpsSdk<SdkProperties> addElixirSdk(@NotNull JpsSdk<?> erlangSdk) {
        @SuppressWarnings("UnstableApiUsage")
        JpsTypedLibrary<JpsSdk<SdkProperties>> elixirTypedLibrary = myModel
                .getGlobal()
                .addSdk("Elixir " + elixirVersion(), elixirSdkHome(), elixirVersion(), Elixir.INSTANCE);

        //noinspection UnstableApiUsage
        eachEbinPath(elixirSdkHome(), ebinPath ->
                elixirTypedLibrary.addRoot(
                        JpsPathUtil.pathToUrl(ebinPath.toAbsolutePath().toString()),
                        JpsOrderRootType.COMPILED
                )
        );

        JpsLibrary erlangSdkLibrary = erlangSdk.getParent();
        elixirTypedLibrary.getProperties().getSdkProperties().setErlangSdkName(erlangSdkLibrary.getName());

        for (JpsLibraryRoot erlangLibraryRoot : erlangSdkLibrary.getRoots(JpsOrderRootType.COMPILED)) {
            //noinspection UnstableApiUsage
            elixirTypedLibrary.addRoot(erlangLibraryRoot.getUrl(), JpsOrderRootType.COMPILED);
        }

        return elixirTypedLibrary.getProperties();
    }

    private JpsSdk<JpsDummyElement> addErlangSdk() {
        String homePath = erlangSdkHome();
        @SuppressWarnings("UnstableApiUsage")
        JpsTypedLibrary<JpsSdk<JpsDummyElement>> erlangTypedLibrary = myModel
                .getGlobal()
                .addSdk("Erlang for Elixir " + otpRelease(), homePath, otpRelease(), Erlang.INSTANCE);
        //noinspection UnstableApiUsage
        eachEbinPath(homePath,
                ebinPath ->
                erlangTypedLibrary.addRoot(
                        JpsPathUtil.pathToUrl(ebinPath.toAbsolutePath().toString()),
                        JpsOrderRootType.COMPILED
                )
        );

        return erlangTypedLibrary.getProperties();
    }

    private static void eachEbinPath(@NotNull String homePath, @NotNull Consumer<Path> ebinPathConsumer) {
        Path lib = Paths.get(homePath, "lib");

        if (!Files.isDirectory(lib)) {
            return;
        }

        try (DirectoryStream<Path> libDirectoryStream = Files.newDirectoryStream(lib, Files::isDirectory)) {
            for (Path app : libDirectoryStream) {
                Path ebin = app.resolve("ebin");
                if (Files.isDirectory(ebin)) {
                    ebinPathConsumer.accept(ebin);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to enumerate ebin paths under " + lib, e);
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        JpsSdk<JpsDummyElement> erlangSdk = addErlangSdk();
        elixirSdk = addElixirSdk(erlangSdk);
    }
}
