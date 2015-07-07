package org.elixir_lang.jps.model;

import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.ex.JpsElementTypeWithDummyProperties;
import org.jetbrains.jps.model.module.JpsModuleType;

/**
 * Created by zyuyou on 2015/5/27.
 * 
 */
public class JpsElixirModuleType extends JpsElementTypeWithDummyProperties implements JpsModuleType<JpsDummyElement>{
  public static final JpsElixirModuleType INSTANCE = new JpsElixirModuleType();

  private JpsElixirModuleType(){

  }
}
