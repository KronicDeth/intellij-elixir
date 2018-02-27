package org.elixir_lang.jps.compiler_options;

import org.elixir_lang.jps.CompilerOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsElementChildRole;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.ex.JpsCompositeElementBase;
import org.jetbrains.jps.model.ex.JpsElementChildRoleBase;

public class Extension extends JpsCompositeElementBase<Extension>{
  private static final JpsElementChildRole<Extension> ROLE = JpsElementChildRoleBase.create("CompilerOptions");

  private CompilerOptions myOptions;

  public Extension(CompilerOptions options) {
    this.myOptions = options;
  }

  @NotNull
  public static Extension getOrCreateExtension(@NotNull JpsProject project){
    Extension extension = project.getContainer().getChild(ROLE);
    if (extension == null){
      extension = project.getContainer().setChild(ROLE, new Extension(new CompilerOptions()));
    }
    return extension;
  }

  @NotNull
  @Override
  public Extension createCopy() {
    return new Extension(new CompilerOptions(myOptions));
  }

  public CompilerOptions getOptions(){
    return myOptions;
  }

  public void setOptions(CompilerOptions options) {
    myOptions = options;
  }
}
