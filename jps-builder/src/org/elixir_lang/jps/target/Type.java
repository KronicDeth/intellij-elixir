package org.elixir_lang.jps.target;

import org.elixir_lang.jps.Target;
import org.elixir_lang.jps.model.JpsElixirModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.BuildTargetLoader;
import org.jetbrains.jps.builders.ModuleBasedBuildTargetType;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsModel;
import org.jetbrains.jps.model.module.JpsTypedModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyuyou on 15/7/10.
 */
public class Type extends ModuleBasedBuildTargetType<Target>{
  public static final Type PRODUCTION = new Type("elixir-production", false);
  public static final Type TEST = new Type("elixir-test", true);

  private final boolean myTests;

  private Type(String elixir, boolean tests){
    super(elixir);
    myTests = tests;
  }

  @NotNull
  @Override
  public List<Target> computeAllTargets(@NotNull JpsModel model) {
    List<Target> targets = new ArrayList<Target>();
    for (JpsTypedModule<JpsDummyElement> module : model.getProject().getModules(JpsElixirModuleType.INSTANCE)){
      targets.add(new Target(this, module));
    }
    return targets;
  }

  @NotNull
  @Override
  public BuildTargetLoader<Target> createLoader(@NotNull final JpsModel model) {
    return new BuildTargetLoader<Target>() {
      @Nullable
      @Override
      public Target createTarget(@NotNull String targetId) {
        for (JpsTypedModule<JpsDummyElement> module : model.getProject().getModules(JpsElixirModuleType.INSTANCE)){
          if(module.getName().equals(targetId)){
            return new Target(Type.this, module);
          }
        }
        return null;
      }
    };
  }

  public boolean isTests(){
    return myTests;
  }
}
