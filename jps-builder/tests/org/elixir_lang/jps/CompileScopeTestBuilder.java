package org.elixir_lang.jps;

import org.jetbrains.jps.builders.BuildTarget;
import org.jetbrains.jps.builders.BuildTargetType;
import org.jetbrains.jps.builders.java.JavaModuleBuildTargetType;
import org.jetbrains.jps.incremental.CompileScope;
import org.jetbrains.jps.incremental.CompileScopeImpl;
import org.jetbrains.jps.incremental.ModuleBuildTarget;
import org.jetbrains.jps.incremental.TargetTypeRegistry;
import org.jetbrains.jps.incremental.artifacts.ArtifactBuildTarget;
import org.jetbrains.jps.incremental.artifacts.ArtifactBuildTargetType;
import org.jetbrains.jps.model.artifact.JpsArtifact;
import org.jetbrains.jps.model.module.JpsModule;

import java.io.File;
import java.util.*;

/**
 * Created by zyuyou on 15/7/17.
 */
public class CompileScopeTestBuilder {
  private final boolean myForceBuild;
  private final Set<BuildTargetType<?>> myTargetTypes = new HashSet<BuildTargetType<?>>();
  private final Set<BuildTarget<?>> myTargets = new HashSet<BuildTarget<?>>();

  public CompileScopeTestBuilder(boolean forceBuild) {
    myForceBuild = forceBuild;
  }

  public static CompileScopeTestBuilder rebuild(){
    return new CompileScopeTestBuilder(true);
  }

  public static CompileScopeTestBuilder make(){
    return new CompileScopeTestBuilder(false);
  }

  public CompileScopeTestBuilder allModules(){
    myTargetTypes.addAll(JavaModuleBuildTargetType.ALL_TYPES);
    return this;
  }

  public CompileScopeTestBuilder module(JpsModule module){
    myTargets.add(new ModuleBuildTarget(module, JavaModuleBuildTargetType.PRODUCTION));
    myTargets.add(new ModuleBuildTarget(module, JavaModuleBuildTargetType.TEST));
    return this;
  }

  public CompileScopeTestBuilder allArtifacts(){
    myTargetTypes.add(ArtifactBuildTargetType.INSTANCE);
    return this;
  }

  public CompileScopeTestBuilder artifact(JpsArtifact artifact){
    myTargets.add(new ArtifactBuildTarget(artifact));
    return this;
  }

  public CompileScopeTestBuilder targetTypes(BuildTargetType<?>... targetTypes){
    myTargetTypes.addAll(Arrays.asList(targetTypes));
    return this;
  }

  public CompileScope build(){
    Collection<BuildTargetType<?>> typesToForceBuild = myForceBuild ? myTargetTypes : Collections.<BuildTargetType<?>>emptyList();
    return new CompileScopeImpl(myTargetTypes, typesToForceBuild, myTargets, Collections.<BuildTarget<?>, Set<File>>emptyMap());
  }

  public CompileScopeTestBuilder all(){
    myTargetTypes.addAll(TargetTypeRegistry.getInstance().getTargetTypes());
    return this;
  }

  public CompileScopeTestBuilder artifacts(JpsArtifact[] artifacts){
    for(JpsArtifact artifact : artifacts){
      myTargets.add(new ArtifactBuildTarget(artifact));
    }
    return this;
  }
}
