package org.elixir_lang.jps.builder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildTargetType;
import org.jetbrains.jps.incremental.BuilderService;
import org.jetbrains.jps.incremental.TargetBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by zyuyou on 15/7/10.
 */
public class ElixirBuilderService extends BuilderService {
  @NotNull
  @Override
  public List<? extends BuildTargetType<?>> getTargetTypes() {
    return Arrays.asList(ElixirTargetType.PRODUCTION, ElixirTargetType.TEST);
  }

  @NotNull
  @Override
  public List<? extends TargetBuilder<?, ?>> createBuilders() {
    return Collections.singletonList(new ElixirBuilder());
  }
}
