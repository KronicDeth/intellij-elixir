package org.elixir_lang.jps;

import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.jps.builder.SourceRootDescriptor;
import org.elixir_lang.jps.target.Type;
import org.elixir_lang.jps.model.ModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.*;
import org.jetbrains.jps.builders.storage.BuildDataPaths;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.indices.IgnoredFileIndex;
import org.jetbrains.jps.indices.ModuleExcludeIndex;
import org.jetbrains.jps.model.JpsModel;
import org.jetbrains.jps.model.java.JavaSourceRootProperties;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.java.JpsJavaClasspathKind;
import org.jetbrains.jps.model.java.JpsJavaExtensionService;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.module.JpsTypedModuleSourceRoot;

import java.io.File;
import java.util.*;

/**
 * Created by zyuyou on 15/7/10.
 */
public class Target extends ModuleBasedTarget<SourceRootDescriptor> {
  public Target(Type targetType, @NotNull JpsModule module) {
    super(targetType, module);
  }

  @Override
  public String getId() {
    return myModule.getName();
  }

  @Override
  public Collection<BuildTarget<?>> computeDependencies(BuildTargetRegistry targetRegistry, TargetOutputIndex outputIndex) {
    return computeDependencies();
  }

  public Collection<BuildTarget<?>> computeDependencies(){
    List<BuildTarget<?>> dependencies = new ArrayList<BuildTarget<?>>();

    Set<JpsModule> modules = JpsJavaExtensionService.dependencies(myModule).includedIn(JpsJavaClasspathKind.compile(isTests())).getModules();
    for (JpsModule module : modules){
      if(module.getModuleType().equals(ModuleType.INSTANCE)){
        dependencies.add(new Target(getElixirTargetType(), module));
      }
    }

    if(isTests()){
      dependencies.add(new Target(Type.PRODUCTION, myModule));
    }

    return dependencies;
  }

  @NotNull
  @Override
  public List<SourceRootDescriptor> computeRootDescriptors(JpsModel model,
                                                           ModuleExcludeIndex index,
                                                           IgnoredFileIndex ignoredFileIndex,
                                                           BuildDataPaths dataPaths) {

    List<SourceRootDescriptor> result = new ArrayList<SourceRootDescriptor>();
    JavaSourceRootType type = isTests() ? JavaSourceRootType.TEST_SOURCE : JavaSourceRootType.SOURCE;
    for(JpsTypedModuleSourceRoot<JavaSourceRootProperties> root : myModule.getSourceRoots(type)){
      result.add(new SourceRootDescriptor(root.getFile(), this));
    }
    return result;
  }

  @Nullable
  @Override
  public SourceRootDescriptor findRootDescriptor(String rootId, BuildRootIndex rootIndex) {
    return ContainerUtil.getFirstItem(rootIndex.getRootDescriptors(new File(rootId), Collections.singletonList(getElixirTargetType()), null));
  }

  @NotNull
  @Override
  public String getPresentableName() {
    return "Elixir '" + myModule.getName() + "' " + (isTests() ? "test" : "production");
  }

  @NotNull
  @Override
  public Collection<File> getOutputRoots(CompileContext context) {
    return ContainerUtil.createMaybeSingletonList(JpsJavaExtensionService.getInstance().getOutputDirectory(myModule, isTests()));
  }

  @Override
  public boolean isTests() {
    return getElixirTargetType().isTests();
  }

  public Type getElixirTargetType(){
    return (Type)getTargetType();
  }
}
