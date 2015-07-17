package org.elixir_lang.jps.builder;

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
public class ElixirTargetType extends ModuleBasedBuildTargetType<ElixirTarget>{
  public static final ElixirTargetType PRODUCTION = new ElixirTargetType("elixir-production", false);
  public static final ElixirTargetType TEST = new ElixirTargetType("elixir-test", true);

  private final boolean myTests;

  private ElixirTargetType(String elixir, boolean tests){
    super(elixir);
    myTests = tests;
  }

  @NotNull
  @Override
  public List<ElixirTarget> computeAllTargets(@NotNull JpsModel model) {
    List<ElixirTarget> targets = new ArrayList<ElixirTarget>();
    for (JpsTypedModule<JpsDummyElement> module : model.getProject().getModules(JpsElixirModuleType.INSTANCE)){
      targets.add(new ElixirTarget(this, module));
    }
    return targets;
  }

  @NotNull
  @Override
  public BuildTargetLoader<ElixirTarget> createLoader(@NotNull final JpsModel model) {
    return new BuildTargetLoader<ElixirTarget>() {
      @Nullable
      @Override
      public ElixirTarget createTarget(@NotNull String targetId) {
        for (JpsTypedModule<JpsDummyElement> module : model.getProject().getModules(JpsElixirModuleType.INSTANCE)){
          if(module.getName().equals(targetId)){
            return new ElixirTarget(ElixirTargetType.this, module);
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
