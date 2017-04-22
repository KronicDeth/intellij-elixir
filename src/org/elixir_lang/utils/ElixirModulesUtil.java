package org.elixir_lang.utils;

import org.jetbrains.annotations.NotNull;

public class ElixirModulesUtil {
  @NotNull
  public static String elixirModuleNameToErlang(@NotNull String moduleName) {
    if (moduleName.equals("true") || moduleName.equals("false") || moduleName.equals("nil")) {
      return moduleName;
    } else if (moduleName.charAt(0) == ':') {
      return moduleName.substring(1);
    } else {
      return "Elixir." + moduleName;
    }
  }

  @NotNull
  public static String erlangModuleNameToElixir(@NotNull String moduleName) {
    if (moduleName.equals("true") || moduleName.equals("false") || moduleName.equals("nil")) {
      return moduleName;
    } else if (moduleName.startsWith("Elixir.")) {
      return moduleName.substring("Elixir.".length());
    } else {
      return ":" + moduleName;
    }
  }
}
