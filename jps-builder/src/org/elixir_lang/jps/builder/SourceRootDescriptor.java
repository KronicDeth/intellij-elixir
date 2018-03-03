package org.elixir_lang.jps.builder;

import com.intellij.openapi.util.io.FileUtilRt;
import org.elixir_lang.jps.Builder;
import org.elixir_lang.jps.Target;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildRootDescriptor;
import org.jetbrains.jps.builders.BuildTarget;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by zyuyou on 15/7/10.
 */
public class SourceRootDescriptor extends BuildRootDescriptor {
  private File myRoot;
  private final Target myTarget;

  public SourceRootDescriptor(File root, Target target) {
    myRoot = root;
    myTarget = target;
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
    return myTarget;
  }

  @NotNull
  @Override
  public FileFilter createFileFilter() {
    return new FileFilter() {
      @Override
      public boolean accept(File file) {
        String name = file.getName();
        return FileUtilRt.extensionEquals(name, Builder.ELIXIR_SOURCE_EXTENSION) ||
            FileUtilRt.extensionEquals(name, Builder.ELIXIR_SCRIPT_EXTENSION);
      }
    };
  }
}
