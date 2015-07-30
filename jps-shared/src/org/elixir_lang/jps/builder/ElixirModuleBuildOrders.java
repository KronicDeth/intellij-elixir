package org.elixir_lang.jps.builder;

import com.intellij.util.xmlb.annotations.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyuyou on 15/7/12.
 */
public class ElixirModuleBuildOrders {
  @Tag("modules")
  public List<ElixirModuleBuildOrderDescriptor> myModuleBuildOrderDescriptors;

  // reflection
  public ElixirModuleBuildOrders(){
    myModuleBuildOrderDescriptors = new ArrayList<ElixirModuleBuildOrderDescriptor>();
  }

  public ElixirModuleBuildOrders(int descriptorsCount){
    myModuleBuildOrderDescriptors = new ArrayList<ElixirModuleBuildOrderDescriptor>(descriptorsCount);
  }
}
