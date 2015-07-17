package org.elixir_lang.jps.builder;

import com.intellij.util.xmlb.annotations.AbstractCollection;
import com.intellij.util.xmlb.annotations.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyuyou on 15/7/12.
 */
public class ElixirModuleBuildOrderDescriptor {
  @Tag("module")
  public String myModuleName = "";

  @Tag("elixirModules")
  @AbstractCollection(surroundWithTag = false, elementTag = "path")
  public List<String> myOrderedElixirModulePaths = new ArrayList<String>();

  @Tag("testElixirModules")
  @AbstractCollection(surroundWithTag = false, elementTag = "path")
  public List<String> myOrderedElixirTestModulePaths = new ArrayList<String>();
}
