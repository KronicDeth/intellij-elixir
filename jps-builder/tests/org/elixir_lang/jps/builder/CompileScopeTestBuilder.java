package org.elixir_lang.jps.builder;

import org.jetbrains.jps.builders.BuildTarget;
import org.jetbrains.jps.builders.BuildTargetType;
import org.jetbrains.jps.incremental.CompileScope;
import org.jetbrains.jps.incremental.CompileScopeImpl;
import org.jetbrains.jps.incremental.TargetTypeRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zyuyou on 15/7/17.
 */
@SuppressWarnings("UnstableApiUsage")
public class CompileScopeTestBuilder {
  private final boolean myForceBuild;
  private final Set<BuildTargetType<?>> myTargetTypes = new HashSet<>();
  private final Set<BuildTarget<?>> myTargets = new HashSet<>();

  public CompileScopeTestBuilder(boolean forceBuild) {
    myForceBuild = forceBuild;
  }

  public static CompileScopeTestBuilder rebuild(){
    return new CompileScopeTestBuilder(true);
  }

  public static CompileScopeTestBuilder make() {
    return new CompileScopeTestBuilder(false);
  }

  public CompileScope build(){
    Collection<BuildTargetType<?>> typesToForceBuild = myForceBuild ? myTargetTypes : Collections.emptyList();
    return new CompileScopeImpl(myTargetTypes, typesToForceBuild, myTargets, Collections.emptyMap());
  }

  public CompileScopeTestBuilder all(){
    myTargetTypes.addAll(TargetTypeRegistry.getInstance().getTargetTypes());
    return this;
  }
}
