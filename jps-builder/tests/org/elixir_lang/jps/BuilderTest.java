package org.elixir_lang.jps;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.PathUtilRt;
import org.elixir_lang.jps.compiler_options.Extension;
import org.elixir_lang.jps.model.ModuleType;
import org.elixir_lang.jps.model.SdkProperties;
import org.elixir_lang.jps.sdk_type.Elixir;
import org.elixir_lang.jps.sdk_type.Erlang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsElement;
import org.jetbrains.jps.model.library.JpsLibrary;
import org.jetbrains.jps.model.library.JpsLibraryRoot;
import org.jetbrains.jps.model.library.JpsOrderRootType;
import org.jetbrains.jps.model.library.JpsTypedLibrary;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.util.JpsPathUtil;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * Created by zyuyou on 15/7/17.
 */
public class BuilderTest extends JpsBuildTestCase {
    private static final String TEST_MODULE_NAME = "m";
    private JpsSdk<SdkProperties> elixirSdk;
    private static String otpReleaseVersion = null;

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
            Process process = Runtime.getRuntime().exec(new String[]{
                    "erl",
                    "-eval",
                    "{ok, Version} = file:read_file(filename:join([code:root_dir(), \"releases\", erlang:system_info(otp_release), \"OTP_VERSION\"])), io:fwrite(Version), halt().",
                    "-noshell"
            });

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                otpRelease = reader.readLine();
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Failed to get OTP release. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get OTP release", e);
        }

        if (otpRelease == null || otpRelease.isEmpty()) {
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
            Process process = Runtime.getRuntime().exec(new String[]{
                    "erl",
                    "-eval",
                    "io:format(\"~s~n\", [code:root_dir()]), halt().",
                    "-noshell"
            });

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                erlangSdkHome = reader.readLine();
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Failed to get Erlang SDK home. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get Erlang SDK home", e);
        }

        if (erlangSdkHome == null || erlangSdkHome.isEmpty()) {
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
                elixirSdk,
                ModuleType.INSTANCE
        );
        rebuildAll();
        String absolutePath = getAbsolutePath("_build/dev/");
        assertNotNull(FileUtil.findFileInProvidedPath(absolutePath,"Elixir.Simple.beam"));
    }

    public void testMix() throws IOException {
        CompilerOptions compilerOptions = Extension.getOrCreateExtension(myModel.getProject()).getOptions();
        compilerOptions.useMixCompiler = true;

        FileUtil.copyDirContent(new File("testData/mix_compiled"), getOrCreateProjectDir());
        addModule(
                "mix_compiled",
                new String[]{getOrCreateProjectDir().getAbsolutePath()},
                getAbsolutePath("_build/dev"),
                getAbsolutePath("_build/test"),
                elixirSdk,
                ModuleType.INSTANCE
        );
        rebuildAll();
        String absolutePath = getAbsolutePath("_build/dev/lib/mix_compiled/ebin/");
        assertNotNull(FileUtil.findFileInProvidedPath(absolutePath, "Elixir.MixCompiled.beam"));
    }

    private JpsSdk<SdkProperties> addElixirSdk(@NotNull JpsSdk erlangSdk) {
        JpsTypedLibrary<JpsSdk<SdkProperties>> elixirTypedLibrary = myModel
                .getGlobal()
                .addSdk("Elixir " + elixirVersion(), elixirSdkHome(), elixirVersion(), Elixir.INSTANCE);

        HomePath.eachEbinPath(elixirSdkHome(), ebinPath ->
                elixirTypedLibrary.addRoot(
                        JpsPathUtil.pathToUrl(ebinPath.toAbsolutePath().toString()),
                        JpsOrderRootType.COMPILED
                )
        );

        JpsLibrary erlangSdkLibrary = erlangSdk.getParent();
        elixirTypedLibrary.getProperties().getSdkProperties().setErlangSdkName(erlangSdkLibrary.getName());

        for (JpsLibraryRoot erlangLibraryRoot : erlangSdkLibrary.getRoots(JpsOrderRootType.COMPILED)) {
            elixirTypedLibrary.addRoot(erlangLibraryRoot.getUrl(), JpsOrderRootType.COMPILED);
        }

        return elixirTypedLibrary.getProperties();
    }

    private JpsSdk<JpsDummyElement> addErlangSdk() {
        String homePath = erlangSdkHome();
        JpsTypedLibrary<JpsSdk<JpsDummyElement>> erlangTypedLibrary = myModel
                .getGlobal()
                .addSdk("Erlang for Elixir " + otpRelease(), homePath, otpRelease(), Erlang.INSTANCE);
        HomePath.eachEbinPath(homePath,
                ebinPath ->
                erlangTypedLibrary.addRoot(
                        JpsPathUtil.pathToUrl(ebinPath.toAbsolutePath().toString()),
                        JpsOrderRootType.COMPILED
                )
        );

        return erlangTypedLibrary.getProperties();
    }

    @Override
    protected JpsSdk<? extends JpsElement> addJdk(String name, String path) {
        throw new IllegalArgumentException("Adding JDK by name alone not supported");
    }

    @Override
    protected <T extends JpsElement> JpsModule addModule(String moduleName,
                                                         String[] srcPaths,
                                                         @Nullable String outputPath,
                                                         @Nullable String testOutputPath,
                                                         JpsSdk<T> sdk) {
        return super.addModule(moduleName, srcPaths, outputPath, testOutputPath, sdk, ModuleType.INSTANCE);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        JpsSdk<JpsDummyElement> erlangSdk = addErlangSdk();
        elixirSdk = addElixirSdk(erlangSdk);
    }
}
