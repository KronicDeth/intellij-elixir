package org.elixir_lang.jps.model;

import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.serialization.JpsProjectExtensionSerializer;

/**
 * Created by zyuyou on 15/7/6.
 */
public class JpsElixirCompilerOptionsSerializer extends JpsProjectExtensionSerializer {
  public static final String COMPILER_OPTIONS_COMPONENT_NAME = "ElixirCompilerOptions";

  public JpsElixirCompilerOptionsSerializer() {
    super("compiler.xml", COMPILER_OPTIONS_COMPONENT_NAME);
  }

  @Override
  public void loadExtension(@NotNull JpsProject project, @NotNull Element componentTag) {
    JpsElixirCompilerOptionsExtension extension = JpsElixirCompilerOptionsExtension.getOrCreateExtension(project);
    ElixirCompilerOptions options = XmlSerializer.deserialize(componentTag, ElixirCompilerOptions.class);
    if(options != null){
      extension.setOptions(options);
    }
  }

  @Override
  public void saveExtension(@NotNull JpsProject project, @NotNull Element componentTag) {
  }
}
