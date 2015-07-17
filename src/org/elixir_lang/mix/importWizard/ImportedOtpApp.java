package org.elixir_lang.mix.importWizard;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.mix.util.ElixirScriptFileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zyuyou on 15/7/2.
 */
public class ImportedOtpApp {
  private final String myName;
  private final VirtualFile myRoot;
  private final Set<String> myDeps = ContainerUtil.newHashSet();
  private VirtualFile myIdeaModuleFile;
  private Module myModule;

  public ImportedOtpApp(@NotNull VirtualFile root, @NotNull final VirtualFile appMixFile){
    // todo: should use psi-pattern to get myName.
    String appName = "";
    try{
      String firstLine = ElixirScriptFileUtil.readLine(appMixFile.getInputStream());
      Pattern pattern = Pattern.compile("defmodule ([A-Z]{1}\\w*).Mixfile do");
      Matcher matcher = pattern.matcher(firstLine);
      appName = matcher.matches() ? matcher.group(1).toLowerCase() : "";
    }catch (Exception ignored){
    }

    myName = appName;
    myRoot = root;

    ApplicationManager.getApplication().runReadAction(new Runnable() {
      @Override
      public void run() {
        addInfoFromAppMixFile(appMixFile);
      }
    });
  }

  @NotNull
  public String getName(){
    return myName;
  }

  @NotNull
  public VirtualFile getRoot(){
    return myRoot;
  }

  @NotNull
  public Set<String> getDeps(){
    return myDeps;
  }

  public void setIdeaModuleFile(@Nullable VirtualFile ideaModuleFile){
    myIdeaModuleFile = ideaModuleFile;
  }

  @Nullable
  public VirtualFile getIdeaModuleFile(){
    return myIdeaModuleFile;
  }

  public Module getModule(){
    return myModule;
  }

  public void setModule(Module module){
    myModule = module;
  }

  @Override
  public String toString() {
    return myName + " (" + myRoot + ")" ;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    ImportedOtpApp that = (ImportedOtpApp) obj;

    return myName.equals(that.myName) && myRoot.equals(that.myRoot);
  }

  @Override
  public int hashCode() {
    int result = myName.hashCode();
    result = 31 * result + myRoot.hashCode();
    return result;
  }

  private void addInfoFromAppMixFile(@NotNull VirtualFile appMixFile){
    // todo: get deps
  }

}
