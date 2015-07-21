package org.elixir_lang.jps.builder;

import com.intellij.openapi.util.io.FileUtilRt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildRootDescriptor;
import org.jetbrains.jps.builders.BuildTarget;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by zyuyou on 15/7/10.
 */
public class ElixirSourceRootDescriptor extends BuildRootDescriptor {
  private File myRoot;
  private final ElixirTarget myElixirTarget;

  public ElixirSourceRootDescriptor(File root, ElixirTarget elixirTarget) {
    myRoot = root;
    myElixirTarget = elixirTarget;
  }

  @Override
  public String getRootId() {
    return myRoot.getAbsolutePath();
  }

  @Override
  public File getRootFile() {
    return myRoot;
  }

  @Override
  public BuildTarget<?> getTarget() {
    return myElixirTarget;
  }

  @NotNull
  @Override
  public FileFilter createFileFilter() {
    return new FileFilter() {
      @Override
      public boolean accept(File file) {
        String name = file.getName();
        return FileUtilRt.extensionEquals(name, ElixirBuilder.ELIXIR_SOURCE_EXTENSION) ||
            FileUtilRt.extensionEquals(name, ElixirBuilder.ELIXIR_SCRIPT_EXTENSION);
      }
    };
  }
}
