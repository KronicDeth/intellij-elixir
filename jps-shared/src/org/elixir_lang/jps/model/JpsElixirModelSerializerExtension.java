package org.elixir_lang.jps.model;

import org.elixir_lang.jps.mix.JpsMixSettingsSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.serialization.JpsModelSerializerExtension;
import org.jetbrains.jps.model.serialization.JpsProjectExtensionSerializer;
import org.jetbrains.jps.model.serialization.library.JpsSdkPropertiesSerializer;
import org.jetbrains.jps.model.serialization.module.JpsModulePropertiesSerializer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by zyuyou on 2015/5/27.
 * https://github.com/ignatov/intellij-erlang/blob/master/jps-shared/src/org/intellij/erlang/jps/model/JpsErlangModelSerializerExtension.java
 */
public class JpsElixirModelSerializerExtension extends JpsModelSerializerExtension{
  public static final String ELIXIR_SDK_TYPE_ID = "Elixir SDK";

  @NotNull
  @Override
  public List<? extends JpsModulePropertiesSerializer<?>> getModulePropertiesSerializers() {
    return Collections.singletonList(new JpsModulePropertiesSerializer<JpsDummyElement>(JpsElixirModuleType.INSTANCE, "ELIXIR_MODULE", null) {
      @Override
      public JpsDummyElement loadProperties(@Nullable Element componentElement) {
        return JpsElementFactory.getInstance().createDummyElement();
      }

      @Override
      public void saveProperties(@NotNull JpsDummyElement properties, @NotNull Element componentElement) {

      }
    });
  }

  @NotNull
  @Override
  public List<? extends JpsSdkPropertiesSerializer<?>> getSdkPropertiesSerializers() {
    return Collections.singletonList(new JpsSdkPropertiesSerializer<JpsDummyElement>(ELIXIR_SDK_TYPE_ID, JpsElixirSdkType.INSTANCE) {
      @NotNull
      @Override
      public JpsDummyElement loadProperties(@Nullable Element propertiesElement) {
        return JpsElementFactory.getInstance().createDummyElement();
      }

      @Override
      public void saveProperties(@NotNull JpsDummyElement properties, @NotNull Element element) {
      }
    });
  }

  @NotNull
  @Override
  public List<? extends JpsProjectExtensionSerializer> getProjectExtensionSerializers() {
    return Arrays.asList(new JpsMixSettingsSerializer(), new JpsElixirCompilerOptionsSerializer());
  }
}
