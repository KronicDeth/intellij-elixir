package org.elixir_lang.jps;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.util.containers.CollectionFactory;
import org.jetbrains.jps.builders.impl.logging.ProjectBuilderLoggerBase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Set;



/**
 * Created by zyuyou on 15/7/17.
 */
public class TestProjectBuilderLogger extends ProjectBuilderLoggerBase {
  private HashMap<String, Set<String>> myCompiledFiles = new HashMap<String, Set<String>>();
  private Set<String> myDeletedFiles = CollectionFactory.createFilePathSet();

  @Override
  public void logDeletedFiles(Collection<String> paths) {
    for (String path:paths){
      myDeletedFiles.add(new File(path).getAbsolutePath());
    }
  }

  @Override
  public void logCompiledFiles(Collection<File> files, String builderName, String description) throws IOException {
    List<String> paths = new ArrayList<String>();
    for (File file: files) {
      paths.add(file.getPath());
    }
    myCompiledFiles.put(builderName, CollectionFactory.createFilePathSet(paths));
  }

  @Override
  protected void logLine(String message) {
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public void clear(){
    myCompiledFiles.clear();
    myDeletedFiles.clear();
  }

  public void assertCompiled(String builderName, File[] baseDirs, String... paths){
    assertRelativePaths(baseDirs, myCompiledFiles.get(builderName), paths);
  }

  public void assertDeleted(File[] baseDirs, String... paths){
    assertRelativePaths(baseDirs, myDeletedFiles, paths);
  }

  private static void assertRelativePaths(File[] baseDirs, Set<String> files, String[] expected){
    List<String> relativePaths = new ArrayList<String>();
    for (String file: files) {
      String path = new File(file).getAbsolutePath();
      for(File baseDir:baseDirs){
        if(baseDir != null && FileUtil.isAncestor(baseDir.getPath(), file, false)){
          path = FileUtil.getRelativePath(baseDir.getPath(), file, File.separatorChar);
          break;
        }
      }
      if (path != null) {
        relativePaths.add(FileUtil.toSystemIndependentName(path));
      }
    }
    UsefulTestCase.assertSameElements(relativePaths, expected);

  }
}
