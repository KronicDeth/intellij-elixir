package org.elixir_lang.jps.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsElementChildRole;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.ex.JpsCompositeElementBase;
import org.jetbrains.jps.model.ex.JpsElementChildRoleBase;

/**
 * Created by zyuyou on 15/7/6.
 */
public class JpsElixirCompilerOptionsExtension extends JpsCompositeElementBase<JpsElixirCompilerOptionsExtension>{
  public static final JpsElementChildRole<JpsElixirCompilerOptionsExtension> ROLE = JpsElementChildRoleBase.create("ElixirCompilerOptions");

  private ElixirCompilerOptions myOptions;

  public JpsElixirCompilerOptionsExtension(ElixirCompilerOptions options) {
    this.myOptions = options;
  }

  @NotNull
  @Override
  public JpsElixirCompilerOptionsExtension createCopy() {
    return new JpsElixirCompilerOptionsExtension(new ElixirCompilerOptions(myOptions));
  }

  public ElixirCompilerOptions getOptions(){
    return myOptions;
  }

  public void setOptions(ElixirCompilerOptions options) {
    myOptions = options;
  }

  @NotNull
  public static JpsElixirCompilerOptionsExtension getOrCreateExtension(@NotNull JpsProject project){
    JpsElixirCompilerOptionsExtension extension = project.getContainer().getChild(ROLE);
    if (extension == null){
      extension = project.getContainer().setChild(ROLE, new JpsElixirCompilerOptionsExtension(new ElixirCompilerOptions()));
    }
    return extension;
  }

}
