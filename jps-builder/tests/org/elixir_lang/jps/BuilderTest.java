package org.elixir_lang.jps;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.PathUtilRt;
import org.elixir_lang.jps.compiler_options.Extension;
import org.elixir_lang.jps.model.ModuleType;
import org.elixir_lang.jps.model.SdkProperties;
import org.elixir_lang.jps.sdk_type.Elixir;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsElement;
import org.jetbrains.jps.model.library.JpsOrderRootType;
import org.jetbrains.jps.model.library.JpsTypedLibrary;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.util.JpsPathUtil;

import java.io.File;

/**
 * Created by zyuyou on 15/7/17.
 */
public class BuilderTest extends JpsBuildTestCase {
  private static final String TEST_MODULE_NAME = "m";

  public void testSimple() {
    CompilerOptions compilerOptions = Extension.getOrCreateExtension(myModel.getProject()).getOptions();
    compilerOptions.useMixCompiler = false;

    doSingleFileTest("lib/simple.ex", "defmodule Simple do def foo() do :ok end end", "Elixir.Simple.beam");
  }

  @Override
  protected JpsSdk<SdkProperties> addJdk(String name, String path) {
    String sdkHome = sdkHome();
    String elixirVersion = elixirVersion();
    JpsTypedLibrary<JpsSdk<SdkProperties>> sdk = myModel
            .getGlobal()
            .addSdk("Elixir: " + elixirVersion, sdkHome, elixirVersion, Elixir.INSTANCE);
    sdk.addRoot(JpsPathUtil.pathToUrl(sdkHome), JpsOrderRootType.COMPILED);

    return sdk.getProperties();
  }

  @Override
  protected <T extends JpsElement> JpsModule addModule(String moduleName,
                                                       String[] srcPaths,
                                                       @Nullable String outputPath,
                                                       @Nullable String testOutputPath,
                                                       JpsSdk<T> sdk) {
    return super.addModule(moduleName, srcPaths, outputPath, testOutputPath, sdk, ModuleType.INSTANCE);
  }

  @NotNull
  private static String sdkHome(){
    return sdkHomeFromEbinDirectory(ebinDirectory());
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

  private void doSingleFileTest(String relativePath, String text, String expectedOutputFileName){
    String depFile = createFile(relativePath, text);
    addModule(TEST_MODULE_NAME, PathUtilRt.getParentPath(depFile));
    rebuildAll();
    assertCompiled(TEST_MODULE_NAME, expectedOutputFileName);
  }

  private void assertCompiled(@NotNull String moduleName, @NotNull String fileName){
    String absolutePath = getAbsolutePath("out/production/" + moduleName);
    assertNotNull(FileUtil.findFileInProvidedPath(absolutePath, fileName));
  }

  private static String elixirVersion() {
        String elixirVersion = System.getenv("ELIXIR_VERSION");

        assertNotNull("ELIXIR_VERSION is not set", elixirVersion);

        return elixirVersion;
    }
}
