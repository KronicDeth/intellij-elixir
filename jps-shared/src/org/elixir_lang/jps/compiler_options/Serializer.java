package org.elixir_lang.jps.compiler_options;

import com.intellij.util.xmlb.XmlSerializer;
import org.elixir_lang.jps.CompilerOptions;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.serialization.JpsProjectExtensionSerializer;

public class Serializer extends JpsProjectExtensionSerializer {
  public static final String COMPILER_OPTIONS_COMPONENT_NAME = "CompilerOptions";

  public Serializer() {
    super("compiler.xml", COMPILER_OPTIONS_COMPONENT_NAME);
  }

  @Override
  public void loadExtension(@NotNull JpsProject project, @NotNull Element componentTag) {
    Extension extension = Extension.getOrCreateExtension(project);
    CompilerOptions options = XmlSerializer.deserialize(componentTag, CompilerOptions.class);

    if(options != null){
      extension.setOptions(options);
    }
  }

  @Override
  public void saveExtension(@NotNull JpsProject project, @NotNull Element componentTag) {
  }
}
